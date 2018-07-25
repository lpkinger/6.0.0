Ext.QuickTips.init();
Ext.define('erp.controller.common.BatchDealer', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.batchDeal.ViewPorter','common.batchDeal.Former','common.batchDeal.GriderPanel','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton','common.batchDeal.ResultGrid',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV','core.button.DeblockSplit','core.button.HandLocked','common.batchDeal.Toolbar'		
     		],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
    		'erpVastEmpowerProdSalerButton':{
    			click:function(btn){
    				me.ConfirmProductSale();
    				var ps_emcode = Ext.getCmp("ps_emcode");
    				me.vastDeal('scm/vastEmpowerProdSaler.action?ps_emcode='+ps_emcode.value);
    			}
    		},'erpVastUnPowerProdSalerButton':{
    			click:function(btn){
    				me.ConfirmProductSale();
    				var ps_emcode = Ext.getCmp("ps_emcode");
    				me.vastDeal('scm/vastUnPowerProdSaler.action?ps_emcode='+ps_emcode.value);
    			}
    		},
    		'erpResultGrid': {
	   			afterrender:function(grid){
	   				store.on('datachanged',function(store){
		            	selectRecord(grid);
		          	});
	   				grid.reconfigure(store,columns);
	   				grid.selModel.selectAll();
	   			}
	   		},
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
    		'erpBatchDealerFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealerGridPanel');
    				me.resize(form, grid);
    				/**
    				 * 手动加锁
    				 */
    				var sacode = getUrlParam("sacode");
    				var prodcode = getUrlParam("prodcode"),detno = getUrlParam("detno"),ob_noallqty=getUrlParam("ob_noallqty"),prodname=getUrlParam("prodname");
    				if(caller=="HandLocked!Deal"&&sacode&&prodcode){
    					var prodcode_ = Ext.getCmp("prodcode_"),code_ = Ext.getCmp("code_"),pr_name = Ext.getCmp("pr_name"),qty=Ext.getCmp("qty"),pd_detno=Ext.getCmp("pd_detno");
    					prodcode_.setValue(prodcode);
    					code_.setValue(sacode);
    					pr_name.setValue(prodname);
    					qty.setValue(ob_noallqty);
    					pd_detno.setValue(detno);
    				}
//    				form.onQuery();
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
					if(!form.tempStore){
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
    		'erpBatchDealerGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('dealerform');
    				me.resize(form, grid);
    				grid.store.on('datachanged', function(store){//dataChanged事件
						me.getProductWh(grid);
					});
    				if(caller == 'ARBill!ToBillOut!Deal'||caller == 'APBill!ToBillOutAP!Deal'){
        				grid.plugins[0].on('afteredit',function(){
        					me.countAmount(grid);
        					
        				});
        				grid.on('selectionchange',function(){
        					me.countAmount(grid);
        				});
    				}
    			},
    			edit:function(ed,d){
    				if(caller == "UpdateMakeSubMaterial" && d.field=='mp_canuseqty'){
	    				//发送请求更新可替代数
	    			  me.updateMakeSub(d);
    				}   				
    			},
    			itemclick: function(selModel, record, item, index, event){//grid行选择
    				if(event.target.getAttribute('class')!='x-grid-row-checker'){
    					if(caller == 'Make!Cost!Deal'){
        			    	url = 'jsps/common/batchDeal.jsp?whoami=Make!OnCost!Deal';
        					if(record) {
        						url += '&ma_code=' + record.data.cd_makecode;
        						url += '&cd_yearmonth=' + record.data.cd_yearmonth;
        						url += '&ma_tasktype=' + record.data.cd_maketype;
        					}
        					me.FormUtil.onAdd('addCostDetailMateria', '月结表', url);
        				}
    				}
    		    }
    		},
    		'field[name=differ]': {
				change: function(field){
					var grid = Ext.getCmp('batchDealerGridPanel');
					me.countAmount(grid);
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
    		'erpVastAnalyseButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastPrintButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastAllotButton':{
    			click:function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpSaveCostDetailButton':{
    			click:function(btn){
    				me.vastDeal('cost/vastSaveCostDetail.action');
    			}
    		},
    		'erpDifferVoucherCreditButton':{
    			click:function(btn){
    				me.vastDeal('cost/vastDifferVoucherCredit.action');
    			}
    		},
    		'erpNowhVoucherCreditButton':{
    			click:function(btn){
    				me.vastDeal('cost/vastNowhVoucherCredit.action');
    			}
    		},
    		'SchedulerTrigger':{
				afterrender:function(trigger){					
					trigger.setFields=[{field:'va_vecard',mappingfield:'ID'},{field:'va_driver',mappingfield:'VA_DRIVER'}];
				}
			},
    		'erpEndCRMButton':{
    			click:function(btn){
    				me.vastDeal('crm/chanceTurnEnd.action');
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
					} else if(f.name == 'cd_yearmonth' && (caller == 'Make!Cost!Deal' || caller == 'Make!OnCost!Deal')) {
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
			},
			'gridcolumn[dataIndex=md_canuseqty]':{
    			 beforerender:function(column){
    			 }
    		}
    	});
    },
    checkTempStore:function(){//查看暂存区
    	var me = this, grid = Ext.getCmp('batchDealerGridPanel');
    	var checkdata=[];
    	Ext.each(grid.tempStore,function(d){
    		var keys=Ext.Object.getKeys(d);//getKeys(Object object):获取所有对象的key组成的数组.
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
				    			var grid=Ext.getCmp('batchDealerGridPanel'),form=Ext.getCmp('dealerform');
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
				    				delete tempStore[key+'temp'];
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
    	var me = this,grid = Ext.getCmp('batchDealerGridPanel'),form=Ext.getCmp('dealerform');
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
		        	key+=r[k.toString()];
		        });
        		grid.tempStore[key+"temp"]=item;//key+temp作为key，解决key为id值只有数字时没有按照添加顺序排序
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
			form.setHeight(45 + fh);
			grid.setHeight(height - fh - 45);
			this.resized = true;
		}
    },
    countGrid: function(){
    	//重新计算合计栏值
    	var grid = Ext.getCmp('batchDealerGridPanel');
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
    	var me = this, grid = Ext.getCmp('batchDealerGridPanel');
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
    	var form = Ext.getCmp('dealerform');
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
				if(main&&main.getActiveTab()){
					main.getActiveTab().setLoading(true);//loading...
				}else{
					window.parent.parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);
				}
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params: params,
			   		method : 'post',
			   		timeout: 6000000,
			   		callback : function(options,success,response){
			   			if(main&&main.getActiveTab())
							main.getActiveTab().setLoading(false);//loading...
						 else{
							window.parent.parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
						 }
			   			me.dealing = false;
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;			   				
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){			   					
			   					str = str.replace('AFTERSUCCESS', '');	
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealerform').onQuery();
			   				}
			   				showError(str);return;
			   			}
		    			if(localJson.success){
		    				grid.tempStore={};//操作成功后清空暂存区数据
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}
		    				grid.multiselected = new Array();
		   					Ext.getCmp('dealerform').onQuery();
			   				/*Ext.Msg.alert("提示", "处理成功!", function(){
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealerform').onQuery();
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
    getCurrentMonth: function(f, type, con) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: type
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    				if(con != null) {
    					con.setMonthValue(rs.data.PD_DETNO);
    				}
    			}
    		}
    	});
    },
    vastPost: function(grid, records, url) {
    	var me = this, win = Ext.getCmp('win-post');
    	grid._postrecords = records;
    	if(!win) {
    		win = Ext.create('Ext.Window', {
    			id: 'win-post',
    			width: '90%',
    			height: '60%',
    			modal: true,
    			layout: 'anchor',
    			items: [{
    				xtype: 'form',
    				anchor: '100% 100%',
    				bodyStyle: 'background: #f1f1f1;',
    				layout: 'column',
    				autoScroll:true,
    				defaults: {
    					xtype: 'checkbox',
    					margin: '2 10 2 10',
    					columnWidth: .33
    				},
    				items: [{
    					xtype: 'displayfield',
    					fieldLabel: '当前账套',
    					id: 'ma_name'    		
    				},{
    					xtype: 'displayfield',
    					fieldLabel: '账套描述',
    					margin: '2 10 30 10',
    					id: 'ma_function',
    					columnWidth: .65
    				},{
    					xtype: 'displayfield',
    					fieldLabel: '目标账套',
    					columnWidth: 1
    				},{
    					boxLabel: '全选',
    					columnWidth: 1,
    					listeners: {
    						change: function(f) {
    		    				var form = f.up('form');
    		    				form.getForm().getFields().each(function(a){
    		    					if(a.xtype == 'checkbox' && a.id != f.id) {
    		    						a.setValue(f.value);
    		    					}
    		    				});
    		    			}
    					}
    				}]
    			}],
    			buttonAlign: 'center',
    			buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						var w = btn.ownerCt.ownerCt, form = w.down('form'),
							from = form.down('#ma_name').value,
							items = form.query('checkbox[checked=true]'),
							data = new Array();
						Ext.each(items, function(item){
							if (item.ma_name)
								data.push(item.ma_name);
						});
						if(data.length > 0)
							me.post(w, grid, url, from, data.join(','));
					}
				},{
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						btn.ownerCt.ownerCt.hide();
					}
				}]
    		});
    		if(contains(url,'?_out=1',true)) this.getOutMasters(win);
    		else this.getMasters(win);
    	}
    	win.show();
    },
    post: function(w, grid, url, from, to) {
    	var records = grid._postrecords;
    	w.setLoading(true);
		var d = new Array(), f = grid.keyField;
		Ext.each(records, function(r) {
			d.push(r.get(f));
		});
		Ext.Ajax.request({
			url: basePath + url,
			params: {
				caller: caller,
				data: d.join(','),
				to: to
			},
			callback: function(opt, s, r) {
				w.setLoading(false);
				if(s) {
					var rs = Ext.decode(r.responseText);
					if(rs.data) {
						showMessage('提示', rs.data);
					} else {
						alert('抛转成功!');
					}
					grid.multiselected = new Array();
					grid._postrecords = null;
   					Ext.getCmp('dealerform').onQuery();
   					w.hide();
				}
			}
		});
    },
	/**
	 * 加载系统所有账套
	 */
	getMasters: function(win){
		Ext.Ajax.request({
			url: basePath + 'common/getAbleMasters.action',
			method: 'get',
			callback: function(opt, s, res){
				var r = Ext.decode(res.responseText), c = r.currentMaster;
				if(r.masters){
					var form = win.down('form'), items = new Array();
    				for(var i in r.masters) {
    					var d = r.masters[i];
    					if(d.ma_name != c) {
    						if(d.ma_type == 3) {
    							var o = {boxLabel: d.ma_name + '(' + d.ma_function + ')', ma_name: d.ma_name};
            					items.push(o);
    						}
    					} else {
    						form.down('#ma_name').setValue(c);
    						form.down('#ma_function').setValue(d.ma_function);
    					}
    				}
    				form.add(items);
				}
			}
		});
	},
	getOutMasters:function(win){
		Ext.Ajax.request({
			url: basePath + 'common/getOutMasters.action',
			method: 'get',
			callback: function(opt, s, res){
				var r = Ext.decode(res.responseText), c = r.currentMaster;	
				if(r.data){
					var form = win.down('form'), items = new Array();
					Ext.Array.each(r.data,function(d){
						var o = {boxLabel: d.MO_LOCATION + '(' + d.MO_NAME + ')', ma_name: d.MO_LOCATION};
    					items.push(o);
					});
    				form.down('#ma_name').setValue(c);
					//form.down('#ma_function').setValue(d.ma_function);
    				form.add(items);
				}
			
			}
		});
	},
	refreshQty : function(cal) {
		var tab = null;
		switch(cal) {
			case 'Purchase!ToCheckAccept!Deal' :
				tab = 'Purchase';
				break;
			case 'Purchase!ToNotify!Deal' :
				tab = 'Purchase';
				break;
			case 'Sale!ToAccept!Deal':
				tab = 'Sale';
				break;
			case 'SendNotify!ToProdIN!Deal':
				tab = 'SendNotify';
				break;
		}
		var form = Ext.getCmp('dealerform');
		form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'common/resetqty.action',
			params : {
				tab : tab
			},
			callback : function(opt, s, res) {
				form.setLoading(false);
				var r = Ext.decode(res.responseText);
				if (r.success) {
					alert('已转数量重置成功!');
//					form.onQuery();
				}
			}
		});
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
	},
    countAmount: function(grid){
    	var me = this;
    	var items = grid.selModel.selected.items;
    	var countamount=0;
    	var taxsum = 0,
    	    differ = Ext.getCmp('differ');
    		priceFormat = grid.down('gridcolumn[dataIndex=abd_thisvoprice]').format,
    		fsize = (priceFormat && priceFormat.indexOf('.') > -1) ? 
    				priceFormat.substr(priceFormat.indexOf('.') + 1).length : 6;
    	Ext.each(items,function(item,index){
    		var a = Number(item.data['abd_thisvoprice']);
    		var b = Number(item.data['abd_thisvoqty']);
    		var rate = Number(item.data['abd_taxrate']);

    		countamount = countamount + Number(grid.BaseUtil.numberFormat(a*b,2));
    		taxsum = taxsum + Number(grid.BaseUtil.numberFormat((a*b*rate/100)/(1+rate/100),2));
    	});
    	//金额合计   不能填写  自动显示所选数据条目的本次发票数*本次发票单价 的总和
       	Ext.getCmp('pi_amounttotal').setValue(Ext.util.Format.number(countamount, "0.00"));
       	if(differ && !Ext.isEmpty(differ.value)){
       		Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum+differ.value, "0.00"));
       	} else {
       		Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum, "0.00"));
       	}
    },
    
    //确认投放数量，在修改完计划投放数量时候点击按钮，将选中行的数量保存，并且限制不能超过建议变更数
    ConfirmThrowQty:function(){
    	var grid = Ext.getCmp('batchDealerGridPanel');
    	var count=0;
    	if(grid.multiselected.length==0){
    		var items = grid.selModel.getSelection();
            Ext.each(items, function(item, index){
            	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
            		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
            		grid.multiselected.push(item);
            	}
            });
    	}
		var records = Ext.Array.unique(grid.multiselected);
		var gridStore = new Array();
		var dd;
		if(records.length>0){
		   	 Ext.each(records, function(records, index){
		   	 if(records.data.md_prodcode!=''){
		   	  dd=new Object();
		   	  dd['mr_mpsid']=records.data.mr_mpsid;
			  gridStore[index] =  Ext.JSON.encode(dd);
			  count++;
			  }
			});
		   	this.ConfirmThrow(gridStore); 
		   	
		} else {
			showError("没有需要处理的数据!");
			}     	
    },
    
    ComfirmThrow:function(store){
			if(this.throwing) {
				alert('正在执行...不要重复点击!');
				return;
			}
			var me = this, gridstore = store;
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			var btn = Ext.getCmp('erpConfirmThrowQtyButton');
			if(btn) btn.setDisabled(true);
			this.throwing = true;
			Ext.Ajax.request({
		   		url : basePath + "pm/MPSMain/NeedThrow.action",
		   		params: {
		   			mainCode:Ext.getCmp('md_mpscode').value,
		   			caller:caller,
		   			gridStore:unescape(gridstore.toString().replace(/\\/g,"%")),
		   			toWhere:'AUTO',
		   			toCode:Ext.getCmp('md_ordercode').value,
		   			condition:'' 
		   		},
		   		timeout: 60000,
		   		method : 'post',
		   		callback : function(options,success,response){
		   			btn.setDisabled(false);
					me.throwing = false;
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", str);
    	   				} else {
    	   					showError(str);return;
    	   				}
		   			}
	    			if(localJson.success){
	    				if(localJson.log){
	    					showMessage("提示", localJson.log);
	    				}
		   				Ext.Msg.alert("提示", "处理成功!", function(){ 
		   					Ext.getCmp('dealerform').onQuery();
		   				});
		   			}
		   		}
			});
		},
		updateMakeSub:function(d){
			if(d.record.dirty){
				if(Ext.isNumber(d.value) && (d.value==0 ||d.value>0)){
					Ext.Ajax.request({
				   		url : basePath + "pm/make/updateMakeSubMaterial.action",
				   		params: {
				   			data:unescape(escape(Ext.JSON.encode(d.record.data))),
				   			caller:caller
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				d.record.set('mp_canuseqty',d.originalValue);
				   				showError(localJson.exceptionInfo);
				   			}
			    			if(localJson.success){
			    				d.record.commit();
			    				showMessage("提示", "修改锁库数量成功");
				   			}
				   		}
					}); 
				}else{
					d.record.set('mp_canuseqty',d.originalValue);
				}
			}
		}
	,ConfirmProductSale:function(){
    	var ps_emcode = Ext.getCmp("ps_emcode");
    	var me = this, grid = Ext.getCmp('batchDealerGridPanel');
    	if(ps_emcode&&!ps_emcode.allowBlank && ps_emcode.value==""){
    		showError('请先选择业务员再操作！');
    		return;
    	}else{
    		if(!grid.toField){
				grid.toField = new Array();
			}
			grid.toField.push('ps_emcode');
    	}
	}
});