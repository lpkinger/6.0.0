/**
 * 借调
 */	
Ext.define('erp.view.core.button.LendTry',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLendTry',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	requires: ['erp.util.FormUtil'],
    	text: $I18N.common.button.erpLendTryButton,
        width: 110,
        id: 'erpLendTryButton',
        FormUtil: Ext.create('erp.util.FormUtil'),
        handler:function(btn){
        	var me = this;
        	var grid = Ext.getCmp('batchDealGridPanel');
        	var items = grid.getMultiSelected();
        	var type = getUrlParam("type").split(",");
        	var tqty = getUrlParam("tqty").split(",");
        	var ob_id = getUrlParam("id").split(",");
        	var sa_code = getUrlParam("sa_code1").split(",");
        	var ob_sadetno = getUrlParam("ob_sadetno1").split(",");
        	var qty = 0;
        	var aqty = 0;
        	for(var j=0;j<tqty.length;j++){
        		aqty = aqty+parseInt(tqty[j]);
        	}
        	for(var i=0;i<items.length;i++){
        		qty = qty+items[i].data.ob_tqty;
        	}
        	if(qty!=aqty){
        		showError("借调数量之和必须等于"+aqty);
        		return;
        	}
        	var record = grid.selModel.selected.items;
			var jsonFormData = new Array();
			for(var j=0;j<type.length;j++){
				var a = new Object();
				a['ob_id']=ob_id[j];
				a['tqty']=tqty[j];
				a['type']=type[j];
				a['sa_code']=sa_code[j];
				a['ob_sadetno']=ob_sadetno[j];
				jsonFormData.push(Ext.JSON.encode(a));
			}
			var jsonGridData = new Array();
			for(var i=0;i<record.length;i++){
				var data = record[i].data;
			    var r=new Object();
			    r['ob_id']=data.ob_id;
			    r['type']=data.type;
			    r['ob_tqty']=data.ob_tqty; 
			    r['sa_code']=data.sa_code; 
			    r['ob_sadetno']=data.ob_sadetno; 
			    jsonGridData.push(Ext.JSON.encode(r));
			}
			var params=new Object();
			params.formdata = unescape(jsonFormData.toString().replace(/\\/g,"%"));
			params.data = unescape(jsonGridData.toString().replace(/\\/g,"%"));
			Ext.Ajax.request({
		   		url : basePath + 'scm/LendTry.action',
		   		params: {
		   			caller : caller,
		   			formdata: params.formdata,
		   			data:params.data
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
	    			if(success){
	    				window.location.reload();
	    				showMessage('借调','借调成功!');
		   			} else if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				showError(str);
		   				} 
	        		}
			});
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});