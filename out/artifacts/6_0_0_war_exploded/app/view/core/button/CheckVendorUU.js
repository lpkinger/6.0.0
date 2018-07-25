/**
 * 批量获取供应商的UU号，一键获取
 */	
Ext.define('erp.view.core.button.CheckVendorUU',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCheckVendorUUButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'erpCheckVendorUUButton',
    	text: $I18N.common.button.erpCheckVendorUUButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var grid = Ext.getCmp('batchDealGridPanel');
	        var items = grid.selModel.getSelection();
	        Ext.each(items, function(item, index){
	        	if(grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
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
	        	}
	        });
			var records = grid.multiselected;
			if(records.length > 0){
				var data = new Array();
				Ext.each(records, function(record, index){	
					var o = new Object();
						if(grid.keyField){
							o[grid.keyField] = record.data[grid.keyField];
						} 
						if(grid.toField){
							Ext.each(grid.toField, function(f, index){
								var v = Ext.getCmp(f).value;
								if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
									o[f] = v;
								}
							});
						}
						if(grid.necessaryFields){
							Ext.each(grid.necessaryFields, function(f, index){
								var v = record.data[f];
								if(Ext.isDate(v)){
									v = Ext.Date.toString(v);
								}
								o[f] = v;
							});
						}
						data.push(o);
					
				});
				grid.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath + 'scm/vastOpenVendorUU.action',
			   		params: {
			   			data: Ext.encode(data),
			   			caller:caller			   			
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			grid.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   			} 
			   			if(localJson.success){		
			   				if(localJson.log){
			   					showMessage("提示", localJson.log);
			   					grid.multiselected = new Array();
			   					return;
		    				}
			   				Ext.Msg.alert("提示", "处理成功!", function(){
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
			   				});}
			   			
			   		}
				});
			} else {
				showError("请勾选需要的明细!");
			}
		}
	});