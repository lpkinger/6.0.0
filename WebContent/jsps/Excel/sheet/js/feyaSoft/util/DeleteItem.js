/**
 * Copyright(c) 2006-2008, FeyaSoft Inc.
 * ====================================================================
 * Licence @ FeyaSoft, all right reserved
 * ====================================================================
 */
Ext.ns("feyaSoft.util");
 
/**
 * This JS is mainly used to handle delete action 
 *
 * @author fzhuang
 * @Date Oct 7, 2007
 */
feyaSoft.util.DeleteItem = function(config) {

    var panel = Ext.getCmp(config.panel);
    var m = panel.getSelections();
    if(m.length > 0)
    {
        // ask user confirm to delete
    	Ext.Msg.confirm('Message', 
    	    'Do you really want to delete them?', 
    	    function(btn) {
	    	     if(btn == 'yes')
		         {	
					var jsonData = "[";
			        for(var i = 0, len = m.length; i < len; i++){ 
						var ss = "{\"id\":\"" + m[i]['id'] + "\"}";
						if(i==0)
			           		jsonData = jsonData + ss ;
					   	else
							jsonData = jsonData + "," + ss;								
			        }	
					jsonData = jsonData + "]";
					
					// send jsondata [{"id":1,"id":2}] to the server to delete 
					// after delete, return to first page
					panel.deleteData(jsonData);		
			    }
		    } 
		);	
    }
    else
    {
    	Ext.MessageBox.alert('Error', 
    	    'To process delete action, please select at least one item to continue'
    	);
    }       

};