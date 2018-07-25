/**
 * Copyright(c) 2006-2008, FeyaSoft Inc.
 */
Ext.Message = function(){
    var msgCt;

    return {
    	// msg format 1
    	msgStay : function(title, message, time){
	        Ext.Msg.show({
	           msg: message,
	           title: title,
	           closable: true,
	           modal: true,
	           icon: Ext.MessageBox.INFO
	       });
	        setTimeout(function(){
	            Ext.Msg.hide();
	        }, time);    				
    	}    	
   };
}();

var Runner = function(){
    var f = function(v, pbar, btn, count, cb){
        return function(){
            if(v > count){
                cb();
            }else{
                var i = v/count;
            }
       };
    };
    return {
        run : function(pbar, btn, count, cb){
            //btn.dom.disabled = true;
            var ms = 5000/count;
            for(var i = 1; i < (count+2); i++){
               setTimeout(f(i, pbar, btn, count, cb), i*ms);
            }
        }
    }
}();

