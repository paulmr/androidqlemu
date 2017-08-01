	INCLUDE qdos.inc

;;; QL PROM header

        dc.l $4AFB0001          ; magic id

        dc.w 0                  ; no sbasic funcs

        dc.w init               ; init ptr (relative to start of rom)

        dc.w 8
        dc.b "QLEMU004"

;;;  init starts here
init:
        movea.l #$28000, a4
        move.l #0, $48(a4)
	rts                  

;;;  pad out to the correct size
        ORG $4000-2
        dc.w $BEEF

        END
