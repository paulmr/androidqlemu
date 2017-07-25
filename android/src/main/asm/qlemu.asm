	INCLUDE qdos.inc

;;; QL PROM header

        dc.l $4AFB0001

        dc.w 0                  ; no sbasic funcs

        dc.w init                 ; init ptr (relative to start of rom)

        dc.w 8
        dc.b "QLEMU001"

;;;  init starts here
init:
	jsr boot_dev(pc)
	rts

;;; create a dummy boot device
boot_dev:
        MT_ALRES #$28           ; allocate room for linkage block

	move.l a0, a1
	;; A1 now points to the link block address

	lea boot_open(pc),a2
	move.l a2, $1C(a1)

        lea input_output(pc),a2
        move.l a2, $20(a1)
        move.l a2, $24(a1)

	lea $18(a1),a0          ; ready for link
        qtrap #mt_liod, #1

	rts

boot_open:
        jmp .notfound(pc)
        ;; a0 contains the address of the filename
	move.l a0,a3
        lea name(pc),a4
        jsr cmpstr(pc)
        tst.b d0
        bne .notfound

.notfound:
        move.l #ERR_NF,d0
        rts

input_output:
        rts


	INCLUDE util.asm

name:	dc.w 4
	dc.b "BOOT"

;;;  pad out to the correct size
        ORG $4000-1
        dc.b $AB

        END
