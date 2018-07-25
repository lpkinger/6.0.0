/**
 * 批量转送货通知单
 */	
Ext.define('erp.view.core.button.VendVastTurnPurchase',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVendVastTurnPurchaseButton',
		text: '批量转送货通知单',
    	tooltip: '可以转单多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpVendVastTurnPurchaseButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 150,
		handler:function(url){
			var grid = Ext.getCmp('batchDeliveryGridPanel');
	        var items = grid.selModel.getSelection();
	        var bool1 = true;
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		var bool = true;
	        		if(this.data['PD_TQTY'] == null || this.data['PD_TQTY'] == '' || this.data['PD_TQTY'] == 0 || this.data['PD_TQTY'] =='0'){
	        			showError("数量为必填项");
    					bool1 = false;
    					return;
	        		}
	        		/*if(this.data['BOXQTY'] != null && this.data['BOXQTY']!='' && this.data['BOXQTY']!='0' && this.data['BOXQTY']!=0){}*/
	        			/*if(this.data['UNITPACKAGE']  >0){	        				
	        				if(this.data['BOXQTY']%this.data['UNITPACKAGE'] != 0){
	        					showError("外箱数量必须是分装数量的整数倍");
	        					bool1 = false;
	        					return;
	        				}}*/
	        			if(this.data['UNITPACKAGE'] < 0 || this.data['UNITPACKAGE'] %1 !=0){
	        				showError("分装数量必须是整数");
	        				bool1 = false;
	        				return;
	        			}else if (this.data['MANTISSAPACKAGE'] != null && this.data['MANTISSAPACKAGE'] != '' ){
	        				this.data['MANTISSAPACKAGE']=this.data['MANTISSAPACKAGE'].replace(/，/ig,','); 
							var re=/^\d+(,\d+)*$/;
						if(re.test(this.data['MANTISSAPACKAGE'])){
							var str = new Array(); 
							var sum=0;
							str = this.data['MANTISSAPACKAGE'].split( "," );
							for (var i = 0; i < str.length; i++ )
							{
								 sum+=parseFloat(str[i]);
							}}else{
								bool1 = false;
								showError("请输入正确的尾数分装数,例如20,30,40");
								return;
							}
						}else if(this.data['BOXQTY'] < 0 || this.data['BOXQTY'] %1 !=0){
	        				showError("外箱容量必须是整数");
	        				bool1 = false;
	        				return;
	        			}
	        		Ext.each(grid.multiselected, function(){
	        			if(this.data[grid.keyField] == item.data[grid.keyField]){
	        				bool = false;
	        			}
	        		});
	        		if(bool){
	        			delete item.data["PR_DETAIL"];
	        			delete item.data["PR_SPEC"];
	        			delete item.data["PR_BRAND"];
	        			delete item.data["PR_ORISPECCODE"];
	        			grid.multiselected.push(item);
	        		}
	        	}
	        });
	        if(bool1){
			var records = grid.multiselected;
			if(records.length > 0){
				var data = new Array();
				Ext.each(records, function(record, index){
					data.push(record.data);
				});
				data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
				grid.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath + 'vendbarcode/acceptNotify/vastTurnAccptNotify.action',
			   		params: {
			   			data: data,
			   			caller:caller			   			
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			grid.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   				grid.multiselected = new Array();
			   			} else {
			   				if(localJson.success){
			   					if(localJson.log){
			    					showMessage("提示", localJson.log);
			    				}
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery(true);
			   				}
			   			}
			   		}
				});
			} else {
				showError("请勾选需要的明细!");
			}}
		}
	});