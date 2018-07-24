/**
 * 请购转采购平台购买
 */
Ext.define('erp.view.core.button.B2CPurchase',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpB2CPurchaseButton',
		text: $I18N.common.button.erpB2CPurchaseButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'B2CPurchaseButton',
    	groups:'',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 85,
		handler:function(){
			var me = this;
			var records = this.getSelectedRecords();
			if(records.length == 0){
				showError('请勾选需要平台购买的明细');
				return ;
			}else if (records.length > 0){//修改成不同物料可同时下达采购单
				var  c = this.getMixedGroups(records, ['ad_prodcode']);
				me.groups = c;
		       /* if(c.length != 1 ){
		    		showError('不同物料编号须分别下达购买');
		    		return ;
		    	}*/
			}	    	
	    	var qty=0;
	    	Ext.Object.each(records,function(a,key){
	    		qty += key.data.ad_tqty;
	    	});	   	
	    	var dlwin;
	    	//弹出平台采购界面 ，参考平台下单的界面和前台实现的业务，
	    	//确认批次和数量之后，生成ERP的采购单Purchase、PurchaseDetail、平台批次表PurchaseBatch数据
    	   if(!dlwin){
    		 var code = records[0].data.ad_prodcode;
    		 dlwin = new Ext.window.Window({
    					   id : 'dlwin',
    					   title:'平台购买',
						   height: "90%",
						   width: "90%",  
						   modal:true,
    					   maximizable : true,
    					   layout : 'anchor',
    					   buttonAlign : 'center',
    					   items: [{
    						   tag : 'iframe',
    						   frame : true,
    						   anchor : '100% 100%',
    						   layout : 'fit',
    						   html : '<iframe id="iframe_dl_'+caller+'" src="'+basePath+'jsps/b2c/purchase/b2cTurnPurchase.jsp?code='+code+'&whoami=B2CPurchase&qty='+qty+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
    					   }],
    					   listeners:{
				    	       	 beforeshow:function(e){		    	       	 
				    	       	 	//根据选中的物料编号，获取
				    	       	 	//me.getReserve(records[0].data.ad_prodcode);
				    	       	 }			    	       	 
			    	        }
    				   });
    		    dlwin.show();
    		   }			
		},
		getMixedGroups: function(items, fields) {
			var data = new Object(),k,o;
			Ext.Array.each(items, function(d){
				k = '';
				o = new Object();
				Ext.each(fields, function(f){
					k += f + ':' + d.get(f) + ',';
					o[f] = d.get(f);
				});
				if(k.length > 0) {
					if(!data[k]) {
						data[k] = {keys: o, groups: [d],totalqty:d.data['ad_tqty']};
					} else {
						data[k].groups.push(d);
						data[k].totalqty+=d.data['ad_tqty'];
					}
				}
			});
			return Ext.Object.getValues(data);
		},
		getReserve : function(code){
			Ext.Ajax.request({//设置列表caller
				url : basePath + "scm/turnPurchase/getReserveByUUid.action",
				params: {
					pr_code : code					
				},
				method : 'post',
				async: false,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exceptionInfo){
						showError(res.exceptionInfo);return;
					}
					if(res.success){
						Ext.getCmp("reserveGrid").getStore().loadData(res.data);
						//解析价格
						//根据采购的数量自动获取
					}
				}
			});
		},
		getSelectedRecords: function(){
	    	var grid = Ext.getCmp('batchDealGridPanel');
	        var items = grid.selModel.getSelection();
	        grid.multiselected = new Array();
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
	        	}
	        });
			return grid.multiselected;			
		},
		getTurnData:function(){
			var form = Ext.getCmp('dealform');
			var grid = Ext.getCmp('batchDealGridPanel');
			var records = Ext.Array.unique(grid.multiselected);
			var params = new Object();
		    params.caller = caller;
			if(records.length > 0){
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
									o[f] = v;
								}
							});
						}
						if(grid.necessaryFields){
							Ext.each(grid.necessaryFields, function(f, index){
								o[f] = record.data[f];
							});
						}
						data.push(o);
					}
				});
				if(bool){
					params.data = Ext.encode(data);
				}
			}
			return params;
		}	
	});