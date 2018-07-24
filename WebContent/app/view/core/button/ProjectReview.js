Ext.define('erp.view.core.button.ProjectReview',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProjectReviewButton',
		param: [],
		iconCls: 'x-button-icon-confirm',
		id: 'ProjectReviewButton',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(){
			  var form=Ext.getCmp('form');
			  var items=form.items.items;
	    	   var reviewresult="",reviewitem="",index=1,length=items.length,count=0,reviewlength=0,radioValue=0;	   
	    	   Ext.Array.each(items,function(item){
	    		   if(item.groupkind=='review'){
	    		   var checked=item.items.items[1].getChecked()[0];	   
	    		   radioValue=checked.inputValue?checked.inputValue:0;
	    		   if(index<length){
	    			   reviewitem+=item.items.items[0].value+"#";
	    			   reviewresult+=radioValue+"#";
	    			   count+=Number(radioValue);
	    		   }else {
	    			   reviewitem+=item.items.items[0].value;
	    			   reviewresult+=radioValue;
	    			   count+=Number(radioValue);
	    		   }
	    		   if(radioValue!=0){
	    			   reviewlength++;
	    		   }
	    		   index++; 
	    		   }
	    	   });
	    		Ext.Ajax.request({
	    		   		url : basePath + 'plm/projectreview/reviewupdate.action',
	    		   		params : {
	    		   			reviewitem:reviewitem,
	    		   			reviewresult:reviewresult,
	    		   			id:Ext.getCmp('pr_id').value
	    		   		},
	    		   		method : 'post',
	    		   		_noc:1,
	    		   		callback : function(options,success,response){	    		 
	    		   			var localJson = new Ext.decode(response.responseText);
	    	    			if(localJson.success){
	    	    				saveSuccess();
	    		   			} else if(localJson.exceptionInfo){
	    		   				var str = localJson.exceptionInfo;
	    		   					showError(str);	    		   			
	    		   			} else{
	    		   				saveFailure();//@i18n/i18n.js
	    		   			}
	    		   		}
	    		   		
	    			});

		}
	});