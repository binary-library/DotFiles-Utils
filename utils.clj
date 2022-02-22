(require '[clojure.string :as cstr])

(def GIT-REPOS-DIR "git-repos")
(directory GIT-REPOS-DIR)

(defn env
  ([env-name] (env env-name nil))
  ([env-name default]
   "env with default"
   (or (dad/env env-name) default)))

(defn envs [env-names]
  (into {} (map (fn [[k v]] [k (env k v)]) env-names)))

(defn renv [env-name]
  "Require env"
  (let [v (dad/env env-name)]
    (if v v (throw (ex-info "Missing env var" {:value (-> env-name name (cstr/replace "-" "_") cstr/upper-case)})))))

(defn renvs [& env-names]
  (into {} (map (fn [k] [k (renv k)]) env-names)))


;; Taken from dad.logger

(def ^:private *level* (env :log-level :info))
(def ^:private *color* (= "true" (env :log-color "true")))
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
  (log* level (str (apply message level msg more) "\n")))

(def debug* (partial log* :debug))
(def info* (partial log* :info))
(def warn* (partial log* :warn))
(def error* (partial log* :error))

(def debug (partial log :debug))
(def info (partial log :info))
(def warn (partial log :warn))
(def error (partial log :error))
