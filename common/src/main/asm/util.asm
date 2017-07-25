cmpstr:
        moveq #0, d0
	move.w (a3)+,d1
	move.w (a4)+,d2
        cmp d1,d2
        bne .nomatch
.loop:
	tst.w d1
	beq .done
        move.b (a3)+,d3
	move.b (a4)+,d4 
	cmp.b d3,d4
        bne .nomatch
        jmp .loop
.nomatch:
        move #-1, d0
.done:
        rts


	
