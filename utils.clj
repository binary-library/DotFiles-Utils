(require
 '[clojure.string :as cstr])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                ;               General               ;
(defn path-join [& paths]
  (cstr/replace (cstr/join "/" paths) #"/+" "/"))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ; Copied from dad source code. Consistent logging ;
(def ^:private *level* (or (dad/env :log-level) :info))
(def ^:private *color* (= "true" (or (dad/env :log-color) "true")))
(def ^:private levels (zipmap [:debug :info :warn :error :silent] (range)))
(def ^:private color-codes
  {:white 97
   :green 32
   :purple 35
   :red 31
   :black 30})
(def ^:private log-colors
  {:debug :white
   :info :green
   :warn :purple
   :error :red
   :silent :black})

(defn colorize
  [color-key s]
  (if *color*
    (str \u001b "[" (get color-codes color-key) "m" s \u001b "[m")
    s))
(defn- log*
  [level msg]
  (when (<= (get levels *level*) (get levels level))
    (print msg)
    (flush)
    nil))
(defn message
  [level msg & more]
  (let [colorize* (partial colorize (get log-colors level :white))
        messages (cond-> [(colorize* (cstr/upper-case (name level)))
                          ":"
                          msg]
                   more (concat more))]
    (cstr/join " " messages)))
(defn- log
  [level msg & more]
  (log* level (str (apply message level msg more) "\n"))
  more)

(def debug* (partial log* :debug))
(def info* (partial log* :info))
(def warn* (partial log* :warn))
(def error* (partial log* :error))

(def debug (partial log :debug))
(def info (partial log :info))
(def warn (partial log :warn))
(def error (partial log :error))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                ;   Environment variable helpers     ;
(defn env*
  ([env-name] (env* env-name nil))
  ([env-name default]
   "env with default"
   (debug "ENV" env-name (or (dad/env env-name) default))))
(defn env [& env-names]
  (if (= (mod (count env-names) 2))
    (into {} (map (fn [[k v]] [k (env* k v)]) (partition 2 env-names)))
    (throw
     (ex-info
      "Missing default value for non-required environment variable"
      {:value env-names}))))
(defn renv* [env-name]
  "Require env"
  (debug "Required ENV" env-name (let [v (dad/env env-name)]
    (if-not v
      (throw
       (ex-info
        "Missing env var"
        {:value
         (-> env-name name (cstr/replace "-" "_") cstr/upper-case)}))
      v))))
(defn renv [& env-names]
  "Required envs"
  (into {} (map (fn [k] [k (renv* k)]) env-names)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                ;          Git build helpers          ;
(def GIT-REPOS-DIR "git-repos")
(directory GIT-REPOS-DIR)
(defn git-build* [build-data]
  (let [[:keys [name git build-deps libs] :or {build-deps [] libs []}] build-data]
    (package (into build-deps libs))
    (git {:path GIT-REPOS-DIR :url git})))
(defn git-build [& build-datas]
  (map git-build* build-datas))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
