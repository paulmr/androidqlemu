QTRAP:  MACRO tnum, ttype
        MOVE.L tnum,d0
        TRAP ttype
        ENDM

MT_ALRES: MACRO byte_count
        MOVE.L byte_count,d1
        QTRAP #$E, #1
        ENDM

MT_LINK:        MACRO addr
        MOVEA.L addr, a0
        QTRAP $22, #1
        ENDM
