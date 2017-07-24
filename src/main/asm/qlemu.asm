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
	
	RTS
        END
