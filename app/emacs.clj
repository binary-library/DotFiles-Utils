;; Build dependencies
(package
 ["libgccjit"
  "make"
  "git"])

(git-build
  {:name "HarfBuzz"
   :build-deps ["meson" "pkgconfig" "gtk-doc"]
   :libs ["freetype" "glib" "cairo"]
   :git "https://github.com/harfbuzz/harfbuzz.git"
   })

(package
 [;; spell check
  "aspell"
  ;; warp stdin for bad shells
  "rlwrap"
  ;; GNU find
  "findutils"
  ;; find alternative
  "fd"
  ])
