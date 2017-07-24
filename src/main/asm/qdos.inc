;; traps
;;
;; #1

mt_liod equ $20

;; error keys

ERR_NF: equ $-7

;; macros

qtrap:  MACRO tnum, ttype
        move.l tnum,d0
        trap ttype
        ENDM

mt_alres: MACRO byte_count
        move.l byte_count,d1
        qtrap #$e, #1
        ENDM