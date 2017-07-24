QTRAP:  MACRO tnum, ttype
        MOVE.L d0,tnum
        TRAP ttype
        ENDM

MT_ALRES: MACRO byte_count
        MOVE.L byte_count,d1
        QTRAP $E, #1
        ENDM
