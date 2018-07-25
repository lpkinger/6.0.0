/**
 * 供应商快速邀请按钮
 */	
Ext.define('erp.view.core.button.InviteVendors',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpInviteVendorsButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'erpInviteVendorsButton',
    	text : $I18N.common.button.erpInviteVendorsButton,
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
	    	var me = this, grid = Ext.getCmp('batchDealGridPanel');
	    	var checkdata=[];
	    	Ext.each(grid.tempStore,function(d){
	    		var keys=Ext.Object.getKeys(d);
				Ext.each(keys, function(k){
					checkdata.push(d[k]);
				});
	    	});
	        var items = grid.getMultiSelected();
	        if(checkdata.length>0&&items.length>0){
	        	showError('暂存区已经有数据，当前筛选界面勾选的数据无效，请取消勾选或添加到暂存区');
	        	return;
	        }else if(items.length>0){
	        	Ext.each(items, function(item, index){
		        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
		        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
		        			item.index = this.data[grid.keyField];
		        			grid.multiselected.push(item);        
		        	}
		        });
	        }else if(checkdata.length>0){
	        	grid.multiselected=checkdata;
	        }
	        var formStore = new Object();
	    	var form = Ext.getCmp('dealform');
			var records = Ext.Array.unique(grid.multiselected);
			if(records.length > 0){
				var params = new Object();
				params.id=new Array();
				params.caller = caller;
				params.formStore = Ext.JSON.encode(formStore);
				var data = new Array();
				var bool = false;
				Ext.each(records, function(record, index){
					var f = form.fo_detailMainKeyField;
					if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
		        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
		        		||(f && this.data[f] != null && this.data[f] != ''
			        		&& this.data[f] != '0' && this.data[f] != 0)){
						bool = true;
						var o = new Object();
						if(grid.keyField){
							o[grid.keyField] = record.data[grid.keyField];
						} else {
							params.id[index] = record.data[form.fo_detailMainKeyField];
						}
						if(grid.toField){
							Ext.each(grid.toField, function(f, index){
								var v = Ext.getCmp(f).value;
								if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
									if(Ext.isDate(v)){
										v = Ext.Date.toString(v);
									}
									o[f] = v;
								} else {
									o[f] = '';
								}
							});
						}
						if(grid.necessaryFields){
							Ext.each(grid.necessaryFields, function(f, index){
								var v = record.data[f];
								if(Ext.isDate(v)){
									v = Ext.Date.toString(v);
								}
								if(Ext.isNumber(v)){
									v = (v).toString();
								}
								o[f] = v;
							});
						}
						data.push(o);
					}
				});
				if(bool && !me.dealing){
					params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
					me.dealing = true;
					me.ownerCt.ownerCt.ownerCt.setLoading(true);
					Ext.Ajax.request({
				   		url : basePath + "scm/inviteVendors.action",
				   		params: params,
				   		method : 'post',
				   		timeout: 6000000,
				   		callback : function(options,success,response){
				   			me.ownerCt.ownerCt.ownerCt.setLoading(false);
				   			me.dealing = false;
				   			var localJson = new Ext.decode(response.responseText);
				   			console.log(localJson);
				   			if(localJson.exceptionInfo){
				   				var str = localJson.exceptionInfo;			   				
				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){			   					
				   					str = str.replace('AFTERSUCCESS', '');	
				   					grid.multiselected = new Array();
				   					Ext.getCmp('dealform').onQuery();
				   				}
				   				showError(str);return;
				   			}
			    			if(localJson.success){
			    				grid.tempStore={};//操作成功后清空暂存区数据
			    				if(localJson.log){
			    					console.log("localJson.log: ");
			    					console.log(localJson.log);
			    					var logMsg = Ext.decode(localJson.log);
			    					var show ="";
			    					if(logMsg.success){
			    						show+=logMsg.success;
			    					}
			    					if(logMsg.error){
			    						show+=logMsg.error;
			    					}
			    					showMessage("提示", show);
			    				}
			    				grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
				   				/*Ext.Msg.alert("提示", "处理成功!", function(){
				   					grid.multiselected = new Array();
				   					Ext.getCmp('dealform').onQuery();
				   				});*/
				   			}
				   		}
					});
				} else {
					showError("没有需要处理的数据!");
				}
			} else {
				showError("请勾选需要的明细!");
			}
	    }
});