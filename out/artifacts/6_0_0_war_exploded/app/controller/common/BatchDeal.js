Ext.QuickTips.init();
Ext.define('erp.controller.common.BatchDeal', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil', 'erp.util.EventSource'],
    views:[
     		'common.batchDeal.Viewport','common.batchDeal.Form','common.batchDeal.GridPanel','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton','core.trigger.MultiDbfindTrigger','core.button.VastTurnARAPCheck','core.button.SyncSpecial',
     		'core.trigger.TextAreaTrigger','core.button.AlertRevertDeal','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger','core.button.InquiryTurnPrice','core.button.CreateReturnMake',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV','core.button.DeblockSplit','core.button.HandLocked','core.button.BusinessChanceLock','core.button.BusinessChanceRestart'
     		,'core.button.BusinessTripOpen','core.button.PostApplication','core.button.CancelPerformMakeECN','core.button.TurnPerformMakeECN','core.button.BatchUpdateMake'
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
    					form.onQuery();
    				}
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
    		'erpBatchDealGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('dealform');
    				me.resize(form, grid);
    				grid.store.on('datachanged', function(store){//dataChanged事件
						me.getProductWh(grid);
					});
    				if(caller == 'ARBill!ToBillOut!Deal'||caller == 'APBill!ToBillOutAP!Deal' || caller =='ARBill!ToARCheck!Deal' || caller=='ProdInOut!ToARCheck!Deal' || caller=='ProdInOut!ToAPCheck!Deal' || caller=='APBill!ToAPCheck!Deal'){
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
    		    },
    		    itemdblclick : function(selModel, record, item, index, event){
    		    	if(caller=='Application!ToPurchase!Deal'){
    		    		var btn = Ext.getCmp("historyprice");
        		    	btn.getWin(record);
    		    	}else if(caller=='AutoInquiryBack'){
    		    		var btn = Ext.getCmp("Prodhistoryprice");
        		    	btn.getWin(record);
    		    	}
    		    }
    		},
    		'field[name=differ]': {
				change: function(field){
					var grid = Ext.getCmp('batchDealGridPanel');
					me.countAmount(grid);
				}
    		},
    		'field[name=ai_jtcycle]':{
    			focus : function(){
    				Ext.create('erp.view.core.window.SelectInquiryDate');
    			}
    		},
    		'field[name=ppd_type]':{
    			change:function(field){
    				var value = Ext.getCmp('ppd_type').value;
    				var ppd_outtype = Ext.getCmp('ppd_outtype');
    				var ppd_intype = Ext.getCmp('ppd_intype');
    				var txt = '';
    				if(value=='PLWD'){
    					txt = ppd_outtype.getEl();
    					txt.dom.firstChild.innerHTML='<font style="color:#F00">转未认定原因:</font>';
    					Ext.getCmp('ppd_appstatus').value='合格';
    				} else if (value=='PLSX'){
    					txt = ppd_outtype.getEl();
    					txt.dom.firstChild.innerHTML='<font style="color:#F00">转无效原因:</font>';
    					Ext.getCmp('ppd_status').value='有效';
    				} else if (value=='PLRD'){
    					txt = ppd_intype.getEl();
    					txt.dom.firstChild.innerHTML='<font style="color:#F00">转合格原因:</font>';
    					Ext.getCmp('ppd_appstatus').value='未认定';
    				} else if (value=='PLCX'){
    					txt = ppd_intype.getEl();
    					txt.dom.firstChild.innerHTML='<font style="color:#F00">转生效原因:</font>';
    					Ext.getCmp('ppd_status').value='无效';
    				}
    			}
    		},
    		'erpVastDealButton': {
    			click: {
    				fn: function(btn){
    					//执行前检测指定必填字段是否填写
    					var form = btn.ownerCt.ownerCt,arr = new Array();
    					Ext.each(form.items.items, function(f){
    						if (!f.allowBlank && contains(f.logic, 'to:', true) && !f.value) {
    							arr.push(f.fieldLabel);
    						}
    					});
    					if(arr.length > 0){
    						showError(arr.join() +' 为空,不能执行当前操作!');
    						return
    					};
	    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl,btn);
	    			},
	    			lock: 2000
    			}
    		},
    		'erpCleanInvalidButton': {
    			click: function(btn){
    				me.cleanInvalid();
    			}
    		},
    		'erpVastAnalyseButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpInquiryTurnPriceButton':{
    			click:function(btn){
    				var select = Ext.getCmp('batchDealGridPanel').getMultiSelected();
    				var flag = 0;
    				Ext.each(select, function(f){
						if (f.data.kind=='采购询价单') {
							flag = 1;
						}
					});
    				if(flag == 1){
	    				warnMsg("转价格审批后平台供应商将不能再报价，确定转价格审批吗？", function(b){
	    					if(b == 'yes'){
	    						me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
	    					}
	    				});
    				}else{
    					me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    				}
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
    		'erpCancelPerformMakeECNButton':{
    			click:function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpTurnPerformMakeECN':{
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
					} else if(f.name == 'vo_yearmonth' && caller == 'CashFlowSet!NO') {
						type = 'MONTH-A';
						con = Ext.getCmp('condatefield');
					} 
					if(type != ''&&!getUrlParam('cd_yearmonth')) {
						this.getCurrentMonth(f, type, con);
					}else{
						f.setValue(getUrlParam('cd_yearmonth'));
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
			'erpCreateReturnMakeButton': {
				click : function(btn) {
					this.createReturnMake(btn);
				}
			},
			'erpBomSyncButton':　{
				click : function(btn) {
					this.BomSync(btn);
				}
			},
			'erpECNSyncButton':　{
				click : function(btn) {
					this.BomSync(btn,'1');
				}
			},
			'erpBatchUpdateMakeButton': {
				click : function(btn) {
					me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
				}
			},
			'gridcolumn[dataIndex=md_canuseqty]':{
    			 beforerender:function(column){
    			 }
    		}   		
    	});
    },
    cleanInvalid:function(){
    	var me = this,
    		main = parent.Ext.getCmp("content-panel");
    		yearmonth = Ext.getCmp('vo_yearmonth');
    	if(yearmonth&&(yearmonth.value==''||yearmonth.value==null)){
    		showError('请选择期间！');
    		return;
    	}
    	if(!me.dealing){
    	me.dealing = true;
    	main.getActiveTab().setLoading(true);
    	Ext.Ajax.request({
			url : basePath + "/fa/gla/cleanInvalid.action",
			params: {yearmonth:yearmonth.value},
			method : 'post',
			timeout: 6000,
			callback : function(options,success,response){
			   	main.getActiveTab().setLoading(false);
			   	me.dealing = false;
			   	var localJson = new Ext.decode(response.responseText);
			   	if(localJson.exceptionInfo){
			   		var str = localJson.exceptionInfo;
			   		if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){			   					
			   			str = str.replace('AFTERSUCCESS', '');
			   			Ext.getCmp('dealform').onQuery();
			   			}
			   		showError(str);
			   		return;
			   		}
		    	if(localJson.success){
		    			showMessage("提示", "已清除期间:"+yearmonth.value+"无效数据！");
		   				Ext.getCmp('dealform').onQuery();
			   		}
			   	}
		});
		}
    },
    checkTempStore:function(){//查看暂存区
    	var me = this, grid = Ext.getCmp('batchDealGridPanel');
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
			form.setHeight(50 + fh);
			grid.setHeight(height - fh - 50);
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
    vastDeal: function(url,btn){    	
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
	        		if(caller=='Application!ToPurchase!Deal'){
	        			item.index = this.data[grid.keyField];
	        			if(this.data['ad_tqty']>0){
	        				grid.multiselected.push(item); 
	        			}
	        		}else{
	        			item.index = this.data[grid.keyField];
	        			grid.multiselected.push(item);        
	        		}
	        	}
	        });
        }else if(checkdata.length>0){
        	grid.multiselected=checkdata;
        }
        if(caller=='Make!ToQuaCheck!Deal'){
        	var bool = false;
        	var bool2 = false;
        	var ma_whcode = Ext.getCmp('ma_whcode');
        	if(ma_whcode&&ma_whcode.value==''){
        		showError('仓库编码必填');
        		return;
        	}
        	Ext.each(grid.multiselected, function(item, index){
        		rn = this.data['RN'];
	        	if(this.data['ma_thisqty'] == null || this.data['ma_thisqty'] == ''
	        		|| this.data['ma_thisqty'] == '0' || this.data['ma_thisqty'] == 0){
	        		bool = true;
	        		return false;
	        	}else if(this.data['ma_zldcode']!==undefined&&(this.data['ma_zldcode']==''||this.data['ma_zldcode']==null)){
	        		bool2 = true;
	        		return false;
	        	}
	        });
        	if(bool){
        		showError('本次数量必须大于0,行号：'+rn);
        		return false;
        	}else if(bool2){
        		showError('序列号必填,行号：'+rn);
        		return false;
        	}
        }
        var formStore = new Object();
        if(caller=='Purchase!ToAccept!Deal' || caller=='Purchase!ToCheckAccept!Deal'){
        	var pu_refcode = Ext.getCmp("pu_refcode");
        	formStore.pu_refcode = pu_refcode?pu_refcode.value : null;
        	if(pu_refcode&&!pu_refcode.allowBlank && pu_refcode.value==""){
        		showError('请先填写送货单号后再转单！');
        		return;
        	}
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
							var v = Ext.getCmp(f).value,
							format = Ext.getCmp(f).format;
							if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
								if(Ext.isDate(v)){
									if(format){
										v = Ext.Date.format(v, format);
									}else{
										v = Ext.Date.format(v);
									}
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
				if(btn){
		    		btn.setDisabled(true);
		    	}
				params.data = Ext.encode(data);
				console.log(params);
				new erp.util.EventSource({
					url : basePath + url,
					data: params,
					showProgress: data.length > 30,
					onComplete: function(result) {
						if(typeof (f = Ext.getCmp('differ')) !== 'undefined'){
	    					f.setValue('0');
	    				}
	    				if(typeof (f = Ext.getCmp('ab_differ')) !== 'undefined'){
	    					f.setValue('0');
	    				}
	    				grid.tempStore={};//操作成功后清空暂存区数据
	    				window.setTimeout(function(){//解决明细太多提示框卡住问题
	    					if(result.log){
	    						showMessage("提示", result.log);
		    				}else{
		    					showMessage("提示", "操作成功");
		    				}
	    					if(btn){
	    			    		btn.setDisabled(false);
	    			    	}
	    				}, 1000); 
	    				grid.multiselected = new Array();
	    				Ext.getCmp('dealform').onQuery();
					},
					onError: function(error) {
						if(error){
							var errorJson = null;
							try {
					                errorJson = JSON.parse(error);
					            } catch (e) {
					            }
				   			if(errorJson&&errorJson.exceptionInfo){
				   				var str = errorJson.exceptionInfo;			   				
				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){			   					
				   					str = str.replace('AFTERSUCCESS', '');	
				   					grid.multiselected = new Array();
				   					Ext.getCmp('dealform').onQuery();
				   				}
				   				showError(str);
			   					if(btn){
		    			    		btn.setDisabled(false);
		    			    	}
				   				return;
				   			}else{
				   				showMessage("提示", error);
				   				if(btn){
		    			    		btn.setDisabled(false);
		    			    	}
				   			}
						}
					}
				});
//				params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
//				me.dealing = true;
//				var main = parent.Ext.getCmp("content-panel");
//				main.getActiveTab().setLoading(true);//loading...
//				Ext.Ajax.request({
//			   		url : basePath + url,
//			   		params: params,
//			   		method : 'post',
//			   		timeout: 6000000,
//			   		callback : function(options,success,response){
//			   			main.getActiveTab().setLoading(false);
//			   			me.dealing = false;
//			   			var localJson = new Ext.decode(response.responseText);
//			   			if(localJson.exceptionInfo){
//			   				var str = localJson.exceptionInfo;			   				
//			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){			   					
//			   					str = str.replace('AFTERSUCCESS', '');	
//			   					grid.multiselected = new Array();
//			   					Ext.getCmp('dealform').onQuery();
//			   				}
//			   				showError(str);return;
//			   			}
//		    			if(localJson.success){
//		    				grid.tempStore={};//操作成功后清空暂存区数据
//		    				if(localJson.log){
//		    					showMessage("提示", localJson.log);
//		    				}
//		    				grid.multiselected = new Array();
//		   					Ext.getCmp('dealform').onQuery();
//			   				/*Ext.Msg.alert("提示", "处理成功!", function(){
//			   					grid.multiselected = new Array();
//			   					Ext.getCmp('dealform').onQuery();
//			   				});*/
//			   			}
//			   		}
//				});
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
			timeout:300000,
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
   					Ext.getCmp('dealform').onQuery();
   					w.hide();
				}
			},
			failure : function(response,options){
			 	w.setLoading(false);
			 	Ext.Msg.alert('提示','请求超时!');
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
		var form = Ext.getCmp('dealform');
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
					codes: codes.join(','),
					caller:caller
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
    	var countamount=0, taxsum = 0, m = 0;
    	var differ = Ext.getCmp('differ');
    	if(caller == 'ARBill!ToBillOut!Deal'||caller == 'APBill!ToBillOutAP!Deal'){
	    	var priceFormat = grid.down('gridcolumn[dataIndex=abd_thisvoprice]').format,
	    		fsize = (priceFormat && priceFormat.indexOf('.') > -1) ? 
	    				priceFormat.substr(priceFormat.indexOf('.') + 1).length : 8;
	    	Ext.each(items,function(item,index){
	    		var a = Number(item.data['abd_thisvoprice']);
	    		var b = Number(item.data['abd_thisvoqty']);
	    		var rate = Number(item.data['abd_taxrate']);
	    		
	    		m =  grid.BaseUtil.numberFormat(a*(b*100)/100,2);
	    		countamount = countamount + m;
	    		taxsum = taxsum + Number(grid.BaseUtil.numberFormat((m*rate/100)/(1+rate/100),2));
	    	});
	    	//金额合计   不能填写  自动显示所选数据条目的本次发票数*本次发票单价 的总和
	       	Ext.getCmp('pi_amounttotal').setValue(Ext.util.Format.number(countamount, "0.00"));
	       	if(differ && !Ext.isEmpty(differ.value)){
	       		Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum+differ.value, "0.00"));
	       	} else {
	       		Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum, "0.00"));
	       	}
    	} else if(caller=='ProdInOut!ToARCheck!Deal' || caller =='ARBill!ToARCheck!Deal' || caller=='ProdInOut!ToAPCheck!Deal' || caller=='APBill!ToAPCheck!Deal' ){
    		var qtysum = 0, noamounttotal = 0, amount = 0, notaxamount = 0;
    		var	priceFormat = grid.down('gridcolumn[dataIndex=pd_thisvoprice]').format,
    			fsize = (priceFormat && priceFormat.indexOf('.') > -1) ? 
    					priceFormat.substr(priceFormat.indexOf('.') + 1).length : 8;
    		Ext.each(items,function(item,index){
        		var a = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoprice']),fsize);
        		var b = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoqty']),2);
        		var rate = grid.BaseUtil.numberFormat(Number(item.data['pi_rate']),2);
        		var taxrate = grid.BaseUtil.numberFormat(Number(item.data['pd_taxrate']),4);
        		
        		m =  grid.BaseUtil.numberFormat(a*(b*100)/100,2);
        		amount = amount + grid.BaseUtil.numberFormat(m*rate,2); //本币金额
        		countamount = countamount + m;//含税金额
        		qtysum = qtysum + b;
        		noamounttotal = noamounttotal + grid.BaseUtil.numberFormat(m/(1+taxrate/100),2);//不含税金额
        		taxsum = taxsum + grid.BaseUtil.numberFormat(m*taxrate/(100+taxrate),2); //税额
        	});
    		if(caller=='ProdInOut!ToARCheck!Deal' || caller=='APBill!ToAPCheck!Deal'){
    			Ext.getCmp('pi_amounttotal').setValue(Ext.util.Format.number(countamount, "0.00"));
    			Ext.getCmp('pi_counttotal').setValue(Ext.util.Format.number(qtysum, "0.0000"));
    			Ext.getCmp('noamounttotal').setValue(Ext.util.Format.number(noamounttotal, "0.00"));
    		}
    		if(caller =='ARBill!ToARCheck!Deal' || caller=='ProdInOut!ToAPCheck!Deal'){
               	Ext.getCmp('pi_amounttotal').setValue(Ext.util.Format.number(countamount, "0.00"));
               	Ext.getCmp('pi_counttotal').setValue(Ext.util.Format.number(qtysum, "0.0000"));
               	if(differ && !Ext.isEmpty(differ.value)){
    	       		Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum+differ.value, "0.00"));
    	       	} else {
    	       		Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum, "0.00"));
    	       	}
               	Ext.getCmp('noamounttotal').setValue(Ext.util.Format.number(noamounttotal, "0.00"));
    		}
    		if(Ext.getCmp('curr_amount')){
           		Ext.getCmp('curr_amount').setValue(amount);
           	}
    	}
    },
    
    //确认投放数量，在修改完计划投放数量时候点击按钮，将选中行的数量保存，并且限制不能超过建议变更数
    ConfirmThrowQty:function(){
    	var grid = Ext.getCmp('batchDealGridPanel');
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
		   					Ext.getCmp('dealform').onQuery();
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
		},
	createReturnMake:function(btn){
		var grid = Ext.getCmp('batchDealGridPanel');
		var items = grid.selModel.getSelection();
		var d = new Array();
		Ext.Array.each(items, function(item){
			d.push({
				ma_code: item.get('ma_code'),
				ma_id: item.get('ma_id'),
				ma_prodcode: item.get('ma_prodcode')
			});
		});
		if(items.length > 0){
			Ext.Ajax.request({
				url : basePath + "pm/make/createReturnMake.action",
				params : {
					data:Ext.encode(d),
		   			caller:caller
				},
				method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   			}
		   			if(localJson.log){
		   				showMessage('提示', localJson.log);
		   			}
		   		}
			});
		}else{
			showMessage("提示", "请勾选需要退料的工单!");
		}
	},
	BomSync:function(btn){
		var form = Ext.getCmp('dealform');
		var grid = Ext.getCmp('batchDealGridPanel');
    	grid.multiselected = new Array();
		var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		var records = Ext.Array.unique(grid.multiselected);
		var badRec = false;
		var bomid = Ext.getCmp('bo_id').value;
		var pr_code = Ext.getCmp('pr_code').value;
		if(!bomid){
			showError('请选择BOM后再同步!');
			return;
		}
		if(!pr_code){
			showError('该BOM没有物料编号!');
			return;
		}
		Ext.each(records, function(item, index){
			if(item.data.VBM_SYNC=='已同步'&&item.data.VBM_ENABLE!='已生效'){
				badRec = true;
				showError('请勿选择已同步未生效的供应商进行抛砖!');
			}
        });		
        if(badRec){return;}
		if(records.length>0){
			 warnMsg('确定要将BOM抛砖到所选的供应商吗？', function(btn){
			 	if(btn == 'yes'){
					form.setLoading(true);
					var data = new Array();
					Ext.Array.each(records, function(item){
						data.push({
							ve_accesskey: item.get('ve_accesskey'),
							ve_erplink: item.get('ve_erplink'),
							master : item.get('ve_targetmaster'),
							ve_id: item.get('ve_id'),
							ve_code: item.get('ve_code'),
							ve_name: item.get('ve_name'),
							vbm_id : item.get('vbm_id'),
							type:item.get('VBM_ENABLE')=='已生效'?'1':'0'
						});
					});
					Ext.Ajax.request({
						async:false,
						url : basePath + 'common/VisitERP/BomSync.action',
						params: {
							pr_code:pr_code,
							bomid:bomid,
							data:Ext.encode(data)
						},
						method : 'post',
						timeout : 120000,
						callback : function(options,success,response){
							form.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.exceptionInfo){
								showError(localJson.exceptionInfo);return;
							}
							if(localJson.success){
								var sum = records.length;
								var successNum = 0;
								Ext.Array.each(localJson.data, function(item){
									if(item.success){
										successNum++;
									}
								});
								var faildMessage = '';
								if(sum>successNum){
									faildMessage = ',请检查供应商资料配置是否正确！'
								}
								Ext.Msg.alert("提示", "总共抛砖"+sum+"个供应商,成功 " + successNum + "个供应商"+faildMessage, function(){ 
				   					Ext.getCmp('dealform').onQuery();
				   				});
							} else {
								delFailure();
							}
						}
					});
				}
			 });
		}else{
			showError("请勾选数据!");
		} 
	}
});