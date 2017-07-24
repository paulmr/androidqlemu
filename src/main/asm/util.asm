COPYSTR:
	MOVE.W (a3)+,d0
	MOVE.w d0,(a4)+
LOOP:
	TST d0
	BEQ done
	MOVE.b (a3)+,(a4)+
	BRA LOOP
done:	rts


	
