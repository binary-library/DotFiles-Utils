(load-file "utils.clj")
;;vi (load-file "app/core.clj")

(def profile
  (merge
   (renvs
    :full-name :email)
   (envs
    {})))

(info profile)
