	INCLUDE qdos.inc

;;; QL PROM header

        DC.L $4AFB0001

        DC.W 0                  ; no sbasic funcs

        DC.W init                 ; init ptr (relative to start of rom)

        DC.W 8
        DC.B "QLEMU001"

;;;  init starts here
init:
        MT_ALRES #$40           ; alloc 40 bytes

        ;; A0 now points to the link block address
        LEA input_output(PC),A1
        MOVE.L a1, $1C(a0)
        MOVE.L a1, $20(a0)
        MOVE.L a1, $24(a0)
        MOVE.L a1, $28(a0)

	lea NAME(pc),a3
	lea $3c(a0),a4
	jsr COPYSTR(pc)

	MT_LDD a0

	RTS

input_output:
        RTS


	INCLUDE util.asm

NAME:	dc.w 4
	dc.b "BOOT"

;;;  pad out to the correct size
        ORG $4000-1
        DC.B $AB
        END
