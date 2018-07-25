/**
 * 商机收回
 */	
Ext.define('erp.view.core.button.CallBack',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCallBackButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'callbackbtn',
    	text: $I18N.common.button.erpCallBackButton,
    	style: {
    		marginLeft: '10px'
        },
        width: null,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var grid = Ext.getCmp('batchDealGridPanel');
	        var items = grid.selModel.getSelection();
	        var domanArr = new Array();
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		var bool = true;
	        		Ext.each(grid.multiselected, function(){
	        			if(this.data[grid.keyField] == item.data[grid.keyField]){
	        				bool = false;
	        			}
	        		});
	        		if(bool){
	        			grid.multiselected.push(item);
	        		}
	        		domanArr.push(item);
	        	}
	        });
			var records = grid.multiselected;
			if(records.length > 0){
				var id = new Array();
				var bcd_id = new Array();
				var bcdIdArr = '';
				Ext.each(records, function(record, index){
					id.push(record.data['bc_id']);
				});
				Ext.each(domanArr,function(record){
					bcd_id.push(record.data['bcd_id']);
				});
				if(bcd_id.length>0){
					bcdIdArr = bcd_id.join(",");
				}
				//grid.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath + 'crm/chance/callBack.action',
			   		params: {
			   			ids: id.join(","),
			   			caller:caller,
			   			bcdids:bcdIdArr
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			grid.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   			} else {
			   				if(localJson.success){
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery(true);
			   				}
			   			}
			   		}
				});
			} else {
				showError("请勾选需要的明细!");
			}
		}
	});