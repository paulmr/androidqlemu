	INCLUDE qdos.inc

;;; QL PROM header

        DC.L $4AFB0001

        DC.W 0                  ; no sbasic funcs

        DC.W init                 ; init ptr (relative to start of rom)

        DC.W 6
        DC.B "QLEMU_"

;;;  init starts here
init:
        MT_ALRES #$40           ; alloc 40 bytes

        ;; A0 now points to the link block address
        LEA input_output(PC),A1
        MOVE.L A1, $1C(a0)

input_output:
        RTS

        MT_LINK $(a0)

	RTS

;;;  pad out to the correct size
        ORG $4000-1
        DC.B $AB
        END
