Ext.QuickTips.init();
Ext.define('erp.controller.b2c.common.b2cBatchDeal', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'b2c.common.b2cBatchDealForm','b2c.common.b2cBatchDealGrid','b2c.common.b2cPanel',
            'core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV',
     	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
    		'#addToTempStore':{
    			click:function(){
    				this.addToTempStore();
    			}
    		},
    		'#checkTempStore':{
    			click:function(){
    				this.checkTempStore();
    			} 
    		},
    		'erpBatchDealFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealGridPanel');
    				me.resize(form, grid);
    				var items = form.items.items, autoQuery = false;
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							autoQuery = true;
							if(this.xtype == 'dbfindtrigger') {
								this.autoDbfind('form', caller, this.name, this.name + " like '%" + val + "%'");
							}
						}
					});
					if(!form.tempStore&&grid){
						grid.columns[1].hide();
					}
					if(autoQuery) {
						setTimeout(function(){
							form.onQuery();
						}, 1000);
					}
					if(form.source=='allnavigation'){
        				Ext.each(form.dockedItems.items[0].items.items,function(btn){
        					btn.setDisabled(true);
        				});
        			}
    			}  			
    		},
    		'erpBatchDealGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('dealform');
    				me.resize(form, grid);
    				grid.store.on('datachanged', function(store){
						me.getProductWh(grid);
					});
    			}
    		},
    		'erpVastDealButton': {
    			click: { 
    				fn: function(btn){
	    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
	    			},
	    			lock: 2000
    			}
    		},
    		'erpVastPrintButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'monthdatefield': {
				afterrender: function(f) {
					var type = '', con = null;
					if(f.name == 'vo_yearmonth' && caller == 'Voucher!Audit!Deal') {
						type = 'MONTH-A';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'vo_yearmonth' && caller == 'Voucher!ResAudit!Deal') {
						type = 'MONTH-A';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'vo_yearmonth' && caller == 'CashFlowSet') {
						type = 'MONTH-A';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'vm_yearmonth' && caller == 'VendMonth!Cyf!Batch') {
						type = 'MONTH-V';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'cm_yearmonth' && caller == 'CustMonth!Cys!Batch') {
						type = 'MONTH-C';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'cd_yearmonth' && caller == 'Make!Cost!Deal') {
						type = 'MONTH-T';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'pc_yearmonth' && caller == 'ProjectCost!Deal') {
						type = 'MONTH-O';
						con = Ext.getCmp('condatefield');
					}
					if(type != '') {
						this.getCurrentMonth(f, type, con);
					}
				},
    			change: function(f) {
    				if(f.name == 'vo_yearmonth' &&( caller == 'Voucher!Audit!Deal'||caller == 'Voucher!ResAudit!Deal')){
        				if(!Ext.isEmpty(f.value)) {
        					var d = Ext.ComponentQuery.query('condatefield');
        					if(d && d.length > 0)
        						d[0].setMonthValue(f.value);
        				}
    				}

    			}
			},
			'erpRefreshQtyButton': {
				click : function() {
					this.refreshQty(caller);
				}
			}
    	});
    },
    checkTempStore:function(){//查看暂存区
    	var me = this, grid = Ext.getCmp('batchDealGridPanel');
    	var checkdata=[];
    	Ext.each(grid.tempStore,function(d){
    		var keys=Ext.Object.getKeys(d);
			Ext.each(keys, function(k){
				checkdata.push(d[k].data);
			});
    	});
    	var  checkwin=Ext.getCmp('checkwin'+caller);
        if(checkwin){
        	checkwin.show();
        }else{
       	  var checkwin =  Ext.create('Ext.Window', {
	    		id : 'checkwin'+caller,
			    height: "100%",
			    width: "80%",
			    maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
				items: [{
			    	  tag : 'iframe',
			    	  frame : true,
			    	  anchor : '100% 100%',
			    	  layout : 'fit',
			    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/tempStore.jsp?caller=' + caller 
			    	  	+"&condition= " +'' +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			    }],
			    buttons : [{
			    	name: 'cancle',
			    	text : $I18N.common.button.erpCancelButton,
			    	iconCls: 'x-button-icon-delete',
			    	cls: 'x-btn-gray',
			    	listeners: {
				    		click: function(btn) {
				    			var checkgrid=Ext.getCmp('checkwin'+caller).items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("tempStoreGridPanel");
				    			checkgrid.setLoading(true);
				    			var grid=Ext.getCmp('batchDealGridPanel'),form=Ext.getCmp('dealform');
				    			var tempStore = grid.tempStore;
				    			var select=checkgrid.getMultiSelected();
				    			var keys=new Array();
						    	if(form.detailkeyfield){
						    		keys=form.detailkeyfield.split('#');
						    	}else{
						    		keys.push(grid.keyField);
						    	}
						    	var bool=false;
				    			Ext.each(select ,function(s){
				    				var key='';
				    				 Ext.each(keys,function(k){
							        	key+=s.data[k];
							    	});
				    				delete tempStore[key];
				    				checkgrid.getStore().remove(s);
				    				Ext.each(grid.store.data.items, function(item){
				    					Ext.each(keys,function(k){
				    						if(item.data[k]==s.data[k]){
				    							bool=true;
				    						}else{
				    							bool=false;
				    							return false;
				    						}
				    					});
				        				if(bool){
				        					item.set('turned','否');
				        				}
				        			});
				    			});
				    			checkgrid.summary();
				    			checkgrid.setLoading(false);
				    		}
				    	}
			    },{
			    	text :$I18N.common.button.erpExportButton,
			    	iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
			    	handler : function(btn){
			    		var checkgrid=Ext.getCmp('checkwin'+caller).items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("tempStoreGridPanel");		
			    		checkgrid.BaseUtil.exportGrid(checkgrid,checkgrid.title);
			    	}
			  } , {
			    	text : $I18N.common.button.erpCloseButton,
			    	iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler : function(btn){
			    		btn.ownerCt.ownerCt.close();
			    	}
			    }]
			});
			checkwin.show();	    			
		}
    },
    addToTempStore:function(){
    	var me = this,grid = Ext.getCmp('batchDealGridPanel'),form=Ext.getCmp('dealform');
    	grid.setLoading(true);
    	var keys=new Array();
    	if(form.detailkeyfield){
    		keys=form.detailkeyfield.split('#');//唯一标识
    	}else{
    		keys.push(grid.keyField);
    	}
        var items = grid.getMultiSelected();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		var key='';
        		var r=this.data;
		        Ext.each(keys,function(k){
		        	key+=r[k];
		        });
        		grid.tempStore[key]=item;
        		item.set('turned','是');//是否已暂存
        		grid.getSelectionModel().deselect(item);//取消勾选
        	}
        });
        grid.setLoading(false);
	},
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
				fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
			form.setHeight(35 + fh);
			grid.setHeight(height - fh - 35);
			this.resized = true;
		}
    },
    countGrid: function(){
    	//重新计算合计栏值
    	var grid = Ext.getCmp('batchDealGridPanel');
    	Ext.each(grid.columns, function(column){
			if(column.summary){
				var sum = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						sum += Number(item.value);
					}
				});
				Ext.getCmp(column.dataIndex + '_sum').setText(column.text + '(sum):' + sum);
			} else if(column.average) {
				var average = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						average += Number(item.value);
					}
				});
				average = average/grid.store.data.items.length;
				Ext.getCmp(column.dataIndex + '_average').setText(column.text + '(average):' + average);
			} else if(column.count) {
				var count = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						count++;
					}
				});
				Ext.getCmp(column.dataIndex + '_count').setText(column.text + '(count):' + count);
			}
		});
    },
    vastDeal: function(url){
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
    	var form = Ext.getCmp('dealform');
		var records = Ext.Array.unique(grid.multiselected);
		if(records.length > 0){
			if(contains(url,'common/form/vastPost.action',true) || contains(url,'common/vastPostProcess.action',true)) {//流程批量抛转
				this.vastPost(grid, records, url);
				return;
			}
			var params = new Object();
			params.id=new Array();
			params.caller = caller;
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
				var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params: params,
			   		method : 'post',
			   		timeout: 6000000,
			   		callback : function(options,success,response){
			   			main.getActiveTab().setLoading(false);
			   			me.dealing = false;
			   			var localJson = new Ext.decode(response.responseText);
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
		    					showMessage("提示", localJson.log);
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
    },
	
	getProductWh: function(grid) {
		var prodfield = grid.getProdField();
		if(prodfield) {
			var codes = [];
			grid.store.each(function(d){
				codes.push("'" + d.get(prodfield) + "'");
			});
			Ext.Ajax.request({
				url: basePath + 'scm/product/getProductwh.action',
				params: {
					codes: codes.join(',')
				},
				callback: function (opt, s, r) {
					if(s) {
						var rs = Ext.decode(r.responseText);
						if(rs.data) {
							grid.productwh = rs.data;
						}
					}
				}
			});
		}
	}
});