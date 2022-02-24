;; (package "libgccjit")
;; (execute "which brew > brew-out.txt")
;; (execute "brew reinstall libgccjit")
;; (execute "brew config -dv > brew-cfg.txt")
(execute "env | sort > env-dump.txt")
;; (load-file "utils.clj")
;; (debug "loaded utils.clj")

;; (def profile
;;   (merge
;;    (renv
;;     ;; All the required environment variables
;;     :full-name :email)
;;    (env
;;     ;; Environment variables with sane defaults
;;     )))

;; (info "Profile:" profile)
;; (info (path-join "a" "b"))
;; (load-file "app/emacs.clj")
