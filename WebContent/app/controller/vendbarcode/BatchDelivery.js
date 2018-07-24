Ext.QuickTips.init();
Ext.define('erp.controller.vendbarcode.BatchDelivery', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'vendbarcode.batchDelivery.Viewport','vendbarcode.batchDelivery.Form','vendbarcode.batchDelivery.GridPanel','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton','core.trigger.MultiDbfindTrigger','core.button.VastTurnARAPCheck',
     		'core.trigger.TextAreaTrigger','core.button.AlertRevertDeal','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger','core.button.InquiryTurnPrice','core.button.CreateReturnMake',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV','core.button.DeblockSplit','core.button.HandLocked','core.button.BusinessChanceLock','core.button.BusinessChanceRestart','core.form.ReConDateField',
     		'core.trigger.MultiDbfindTrigger','core.trigger.AddDbfindTrigger'
     		],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
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
    		'multidbfindtrigger[name=pu_code]': {
	             afterrender: function(t){
	            	 Ext.Ajax.request({
	            		   url : basePath +'vendbarcode/vendbarcode/getUser.action',
	            		   method : 'get',
	            		   async: false,
	            		   params:{
	             			   caller:caller
	             		   },
	            		   callback : function(options,success,response){
	            			   var rs = new Ext.decode(response.responseText);
	            			   if(rs.exceptionInfo){
	            				   showError(rs.exceptionInfo);return;
	            			   }
	            			   t.dbBaseCondition = 'pu_vendcode=\'' + rs.data + '\'';
	            		   }
	            	   });
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
    		'erpVastDealButton': {
    			click: {
    				fn: function(btn){
	    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
	    			},
	    			lock: 2000
    			}
    		},
    		'erpCleanInvalidButton': {
    			click: function(btn){
    				me.cleanInvalid();
    			}
    		},
    		'SchedulerTrigger':{
				afterrender:function(trigger){					
					trigger.setFields=[{field:'va_vecard',mappingfield:'ID'},{field:'va_driver',mappingfield:'VA_DRIVER'}];
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
			'gridcolumn[dataIndex=md_canuseqty]':{
    			 beforerender:function(column){
    			 }
    		}
    	});
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
         if(items.length>0){
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
    }
});