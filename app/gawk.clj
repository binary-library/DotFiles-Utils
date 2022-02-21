#!/usr/bin/env dad

;; (GNU awk) https://www.gnu.org/software/gawk/manual/html_node/index.html
;; AWKLIBPATH = /usr/local/lib/gawk ;; folders to find extensions
(package
 ["gawk"
  ;; Build tools for extensions
  "autoconf""automake" "gettext" "libtool" "autopoint"])

git clone git://git.code.sf.net/p/gawkextlib/code
(git "dad-source" {:url "https://github.com/liquidz/dad"})
