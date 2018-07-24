Ext.QuickTips.init();
Ext.define('erp.controller.scm.qc.VerifyApplyDetail2', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
      		'core.form.Panel','scm.qc.VerifyApplyDetail2','core.grid.Panel2','scm.qc.ProjectDetGrid','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Print','core.button.ResAudit','core.button.Audit','core.button.Close','core.form.FileField',
      		'core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.TurnMrb',
      		'core.button.Load','core.button.DeleteAllDetails','core.button.Submit','core.button.ResSubmit',
      		'core.button.Check','core.button.ResCheck','core.button.TurnProdAbnormal','core.button.TurnT8DReport',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.button.PrintByCondition'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick : this.onGridItemClick,
    			afterrender: function(g) {
    				g.plugins[0].on('beforeedit', function(args){
    					if(g.readOnly) {
    						return false;
    					}
    					var status = args.record.data.ved_statuscode, isok=args.record.data.ved_isok,
    					isng = args.record.data.ved_isng;
    					if(status == 'AUDITED' || status == 'TURNIN') {
    						return false;
    					}
    					if (args.field == "ved_okqty") {
                            return me.isAllowUpdateOkQty(args.record);
                        }
                        if (args.field == "ved_ngqty") {
                            return me.isAllowUpdateNgQty(args.record);
                        }
    					if(args.field == "ved_okqty" && isok == 1) {
    						return false;
    					}
    					if(args.field == "ved_ngqty" && isng == 1) {
    						return false;
    					}
    				});
    			},
    			reconfigure: function(grid) {
    				if (Ext.getCmp('vad_qty')) {
    					var qty = Ext.getCmp('vad_qty').value,
	    					record = grid.store.getAt(0);
	    				if(record.get('ved_okqty') == 0 && record.get('ved_ngqty') == 0
	    						&& record.get('ved_id') == 0) {
	    					record.set('ved_okqty', qty);
	    				}
    				}
    			}
    		},
    		'#erpUpdateQtyButton': {
 			   afterrender: function(btn){
 				   var status = Ext.getCmp('ve_statuscode');
 				   if(status && status.value == 'UNAUDIT'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
 				   me.updateQty(record);
 			   }
 		    },
    		'projectdet': { 
    			itemclick: this.onGridItemClick1
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
        			var qty = Ext.getCmp('vad_qty').value,
	    				record = grid.store.getAt(0);
	    			if(record.get('ved_okqty') == 0 && record.get('ved_ngqty') == 0) {
	    				record.set('ved_okqty', qty);
	    			}
    				me.beforeUpdate();
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var qty = Ext.getCmp('vad_qty').value,
    					record = grid.store.getAt(0);
    				var errInfo = me.GridUtil.getUnFinish(grid);
    				if(errInfo.length > 0){
    					showError('明细表有必填字段未完成填写');
    					return false;
    				}	
    				if(record.get('ved_okqty') == qty && record.get('ved_id') == 0) {
    					warnMsg('还未保存！是否先保存单据?', function(b){
    						if(b == 'ok' || b == 'yes') {
    							me.beforeUpdate();
    						}
    					});
    				} else {
    					me.FormUtil.onAudit(Ext.getCmp('ve_id').value);
    				}
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_statuscode');
    				if(status && status.value == 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var condition = '{QUA_VerifyApplyDetail.ve_id}=' + Ext.getCmp('ve_id').value + '';
    				var id = Ext.getCmp('ve_id').value;
    				reportName="verifyMake";
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpTurnMrbButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转Mrb单吗?", function(btn){
    					if(btn == 'yes'){
    						var ve_id = Ext.getCmp('ve_id').value;
		    				Ext.Ajax.request({//拿到grid的columns
								url : basePath + "scm/qc/turnMrb.action",
								params: {
									id:ve_id,
									code:Ext.getCmp('ve_code').value
								},
								method : 'post',
								async: false,
								callback : function(options,success,response){
									var localJson = new Ext.decode(response.responseText);
		    			   			if(localJson.exceptionInfo){
		    			   				showError(localJson.exceptionInfo);
		    			   			}
		    		    			if(localJson.success){
		    		    				alert("转Mrb单成功！");
		    		    			}
								}
							});
    					}
    				});
    			}
    		},
    		'erpTurnProdAbnormalButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转物料品质异常联络单吗?", function(btn){
    					if(btn == 'yes'){
    						var ve_id = Ext.getCmp('ve_id').value;
		    				Ext.Ajax.request({//拿到grid的columns
								url : basePath + "scm/qc/turnProdAbnormal.action",
								params: {
									id: ve_id,
									caller: caller
								},
								method : 'post',
								async: false,
								callback : function(options,success,response){
									var localJson = new Ext.decode(response.responseText);
		    			   			if(localJson.exceptionInfo){
		    			   				showError(localJson.exceptionInfo);
		    			   			}
		    		    			if(localJson.success){
		    		    				//alert("转物料品质异常联络单成功！");
		    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/qc/prodAbnormal.jsp?formCondition=pa_id=" + id;
    	    		    					me.FormUtil.onAdd('ProdAbnormal' + id, '物料品质异常联络单' + id, url);
    	    		    				});
		    		    			}
								}
							});
    					}
    				});
    			}
    		},
    		'erpTurnT8DReportButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转8D报告吗?", function(btn){
    					if(btn == 'yes'){
    						var ve_id = Ext.getCmp('ve_id').value;
		    				Ext.Ajax.request({//拿到grid的columns
								url : basePath + "scm/qc/turnT8DReport.action",
								params: {
									id: ve_id,
									caller: caller
								},
								method : 'post',
								async: false,
								callback : function(options,success,response){
									var localJson = new Ext.decode(response.responseText);
		    			   			if(localJson.exceptionInfo){
		    			   				showError(localJson.exceptionInfo);
		    			   			}
		    		    			if(localJson.success){
		    		    				//alert("转8D报告成功！");
		    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/qc/t8dreport.jsp?formCondition=re_id=" + id;
    	    		    					me.FormUtil.onAdd('T8DReport' + id, '8D报告' + id, url);
    	    		    				});
		    		    			}
								}
							});
    					}
    				});
    			}
    		},
    		'erpLoadButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_statuscode');
    				if(status && status.value != 'UNAUDIT'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var veid = Ext.getCmp('ve_id').value;
    				var prid = Ext.getCmp('ve_prid').value;
    				if(prid == 0 || prid == null){
    					Ext.Msg.alert('请先填写检验方案ID!');
    				}
    				warnMsg("确认载入检验项目?", function(btn){
    					if(btn == 'yes'){
    						var params = new Object();
    	    				var form = Ext.getCmp('form');
    	    				var grid = Ext.getCmp('projectdetGrid');
    	    				var items = grid.store.data.items;
    	    				var array = new Array();
    	    				var r = form.getValues();
    	    				Ext.each(items,function(item,index){
    	    					if(item.data['vd_item']!=null&&item.data['vd_item']!=""){
    	    						array.push(item);
    	    					}
    	    				});
    	    				var prid = Ext.getCmp('ve_prid').value;
    	    				if(!prid||(prid&&(prid == 0||prid==''||prid==null))){
    	     					Ext.Msg.alert('请先填写检验方案ID!');
    	     				}else{
    	     					if(array.length>0){
    	        					Ext.Msg.alert('需要先清除明细行中的数据!');
    	        				} else {
    	        					params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    	        					//抓取
    	        					Ext.Ajax.request({
    	        				   		url : basePath + "scm/qc/catchProject.action",
    	        				   		params : {
    										id: veid,
    										prid: prid
    									},
    	        				   		method : 'post',
    	        				   		callback : function(options,success,response){
    	        				   			me.FormUtil.getActiveTab().setLoading(false);
    	        				   			var localJson = new Ext.decode(response.responseText);
    	        			    			if(localJson.success){
    	        			    				catchSuccess(function(){
    	        			    					//add成功后刷新页面进入可编辑的页面 
    	        					   				var value = r[form.keyField];
    	        					   		    	var condition = 'vd_veid='+value;
    	        					   		    	grid.getMyData(condition);
    	        			    				});
    	        				   			} else if(localJson.exceptionInfo){
    	        				   				
    	        				   			} else{
    	        				   				catchFailure();//@i18n/i18n.js
    	        				   			}
    	        			    			window.location.reload();
    	        				   		}
    	        					});
    	        				}
    	     				}
    					}
    				});
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_statuscode');
    				 if (status && status.value != 'UNAUDIT') {
                        btn.hide();
                    }
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ve_id').value,false, me.beforeUpdate, me);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_statuscode');
    				if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onCheck(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpResCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_checkstatuscode');
    				if(status && status.value != 'APPROVE' ){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResCheck(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpDeleteAllDetailsButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_statuscode');
    				if(status && status.value != 'UNAUDIT'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('projectdetGrid');
    				warnMsg('确定清除所有明细行么?',function(t){
    					if(t=='yes'){
    						var veid = Ext.getCmp('ve_id').value;
    						var form = Ext.getCmp('form');
    						var r = form.getValues();
    						var params = new Object();
    						params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    						Ext.Ajax.request({
        				   		url : basePath + "scm/qc/cleanProject.action",
        				   		params :  {
									id: veid
								},
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				catchSuccess(function(){
        			    					//add成功后刷新页面进入可编辑的页面 
        					   				var value = r[form.keyField];
        					   		    	var condition = 'vd_veid='+value;
        					   		    	grid.getMyData(condition);
        			    				});
        				   			} else if(localJson.exceptionInfo){
        				   				
        				   			} else{
        				   				catchFailure();//@i18n/i18n.js
        				   			}
        				   		}
        					});
    					}else{
    						return;
    					}
    				});
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    	var grid = selModel.ownerCt, 
    		btn = grid.down('erpDeleteDetailButton');
		if(btn && record.get('ved_statuscode') == 'TURNIN'){
			btn.setDisabled(true);
		}
		btn = Ext.getCmp('erpUpdateQtyButton');
		if(btn && record.data.ved_statuscode != 'TURNIN'){
			btn && btn.setDisabled(false);
		}
    },
    onGridItemClick1: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('projectdetGrid');
    	grid.lastSelectedRecord = record;
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeUpdate: function(){
		var mm = this;
		if(! mm.FormUtil.checkForm()){
			return;
		}
		var form=Ext.getCmp('form');
		var s1 = mm.FormUtil.checkFormDirty(form);
		var grid2 = Ext.getCmp('projectdetGrid');
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, qty = Ext.getCmp('vad_qty').value,
			testman = Ext.getCmp('ve_testman').value;
		var bool = true;
		//合格数量不能大于送检数量
		Ext.each(items, function(item){
			if(item.dirty){
				item.set('ved_samplingngqtylv', item.data['ved_samplingngqty']*100/item.data['ved_samplingqty']);
			}
		});
		Ext.each(items, function(item){
			if(item.dirty){
				if(item.data['ved_statuscode'] == "AUDITED"){
					bool = false;
					showError('明细表第' + item.data['ved_detno'] + '行已审核，不能修改！');return;
				}
				if(item.data['ved_okqty'] + item.data['ved_ngqty'] > qty){
					bool = false;
					showError('明细表第' + item.data['ved_detno'] + '行的合格数量与不合格数量之和不能大于送检数量！');return;	
				} else {
					item.set('ved_checkqty', item.data['ved_okqty'] + item.data['ved_ngqty']);
				}
				item.set('ved_samplingqty', item.data['ved_samplingokqty'] + item.data['ved_samplingngqty']);
			}
		});
		var sum = grid.store.getSum(items, 'ved_checkqty');
		if(sum > qty) {
			showError('送检数量之和不能大于收料数量!');return;
		}
		var param1 = mm.GridUtil.getGridStore(grid);
		var param2 = mm.GridUtil.getGridStore(grid2);
		//更新
		if(bool){
			if(s1 == '' && (param1 == null || param1 == '') && (param2 == null || param2 == '')){
				warnMsg('未添加或修改数据,是否继续?', function(btn){
					if(btn == 'yes'){
						mm.onUpdate(param1, param2);
					} else {
						return;
					}
				});
			} else {
				mm.onUpdate(param1, param2);
			}
		}
	},
	onUpdate:function(param1,param2){
		var me = this;
		var form = Ext.getCmp('form');
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
			});
			if(!me.FormUtil.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			me.FormUtil.update(r, param1, param2);
		}else{
			me.FormUtil.checkForm();
		}
	},
	isAllowUpdateOkQty: function(record) {
        var bool = true;
        if (caller == 'VerifyApplyDetail!FQC') {
            if (record.get('ved_ngqty') != null && record.get('ved_ngqty') > 0) bool = false;
        }
        return bool;
    },
    isAllowUpdateNgQty: function(record) {
        var bool = true;
        if (caller == 'VerifyApplyDetail!FQC') {
            if (record.get('ved_okqty') != null && record.get('ved_okqty') > 0) bool = false;
        }
        return bool;
    },
    updateQty:function(record){
 	   var win = this.updateQtywindow;
 	   if (!win) {
 		  this.updateQtywindow = win = this.getUpdateQtyWindow(record);
 	   }
 	   win.show();
    },
	getComboData: function(table, field, callback) {
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'DataListCombo',
	   			fields: 'dlc_value,dlc_display',
	   			condition: 'dlc_caller=\'' + table + '\' AND dlc_fieldname=\'' + field + '\''
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}
	  			if(localJson.success){
	  				var data = Ext.decode(localJson.data), arr = new Array();
	  				for(var i in data) {
	  					arr.push({
	  						display: data[i].DLC_VALUE,
	  						value: data[i].DLC_DISPLAY
	  					});
	  				}
	  				callback.call(me, arr);
		   		}
	   		}
		});
	},
    getUpdateQtyWindow : function(record) {
    	var me = this;
    	var win = Ext.create('Ext.window.Window',{
 		   width: 330,
 		   height: 300,
 		   closeAction: 'hide',
 		   cls: 'custom-blue',
 		   title:'<h1>更改数量</h1>',
 		   layout: {
 			   type: 'vbox'
 		   },
 		   items:[{
 			   margin: '5 0 0 5',
 			   xtype:'numberfield',
 			   hideTrigger: true,
 			   fieldLabel:'合格数量',
 			   value: record.data.ved_okqty,
 			   name:'ved_okqty',
 			   id:'ved_okqty'
 		   },{
 			   margin: '5 0 0 5',
 			   xtype:'numberfield',
 			   hideTrigger: true,
 			   fieldLabel:'不合格数量',
 			   value: record.data.ved_ngqty,
 			   name:'ved_ngqty',
 			   id:'ved_ngqty',
 		   },{
 			   margin: '5 0 0 5',
 			   xtype:'combo',
 			   fieldLabel:'不良原因码',
 			   value: record.data.ved_nrcode,
 			   name:'ved_nrcode',
 			   id:'ved_nrcode',
 			   displayField: 'display',
 			   valueField: 'value',
 			   queryMode: 'local',
 			   store : new Ext.data.Store({
 				   fields: ['display', 'value'],
 				   data: []
 			   })
 		   },{
 			   margin: '5 0 0 5',
 			   xtype:'combo',
 			   fieldLabel:'不良处理',
 			   value: record.data.ved_ngdeal,
 			   name:'ved_ngdeal',
 			   id:'ved_ngdeal',
 			   displayField: 'display',
			   valueField: 'value',
			   queryMode: 'local',
			   store : new Ext.data.Store({
				   fields: ['display', 'value'],
				   data: []
			   })
 		   },{
 			   margin: '5 0 0 5',
 			   xtype:'textfield',
 			   fieldLabel:'不合格描述',
 			   value: record.data.ved_remark,
 			   name:'ved_remark',
 			   id:'ved_remark'
 		   }],
 		   buttonAlign:'center',
 		   buttons:[{
 			   xtype:'button',
 			   text:'保存',
 			   width:60,
 			   iconCls: 'x-button-icon-save',
 			   handler:function(btn){
 				   var w = btn.up('window');
 				   me.saveQty(w);
 				   w.hide();
 			   }
 		   },{
 			   xtype:'button',
 			   columnWidth:0.1,
 			   text:'关闭',
 			   width:60,
 			   iconCls: 'x-button-icon-close',
 			   margin:'0 0 0 10',
 			   handler:function(btn){
 				   btn.up('window').hide();
 			   }
 		   }]
 	   });
    	me.getComboData(caller, 'ved_nrcode', function(data){
    		win.down('#ved_nrcode').store.loadData(data);
    	});
    	me.getComboData(caller, 'ved_ngdeal', function(data){
    		win.down('#ved_ngdeal').store.loadData(data);
    	});
    	return win;
    },
    saveQty: function(w) {
 	   var ved_okqty = w.down('field[name=ved_okqty]').getValue(),
 	       ved_ngqty = w.down('field[name=ved_ngqty]').getValue(),
 	       ved_nrcode = w.down('#ved_nrcode').getValue(),
 	       ved_ngdeal = w.down('#ved_ngdeal').getValue(),
 	       qty = Ext.getCmp('vad_qty').value,
 	       ved_remark = w.down('#ved_remark').getValue(),
 	       okqty = ved_okqty ? ved_okqty : 0,
 	       ngqty = ved_ngqty ? ved_ngqty : 0,
 	   	   grid = Ext.getCmp('grid'),
 	       record = grid.getSelectionModel().getLastSelected();
 	  if(okqty+ngqty-qty>0){
 		 showError('合格数量与不合格数量之和不能大于送检数量！');
         return;
 	  }
 	  var dd = {
			   ved_id : record.data.ved_id,
			   ved_veid : record.data.ved_veid,
			   ved_okqty : okqty,
		       ved_ngqty : ngqty,
		       ved_nrcode : ved_nrcode,
		       ved_ngdeal : ved_ngdeal,
		       ved_remark : ved_remark,
			   caller: caller 
	   };
	   Ext.Ajax.request({
		   url : basePath +'scm/qc/updateQty.action',
		   params : {
			   _noc: 1,
			   data: unescape(Ext.JSON.encode(dd))
		   },
		   method : 'post',
		   callback : function(opt, s, res){
			   var r = new Ext.decode(res.responseText);
			   if(r.success){
				   grid.GridUtil.loadNewStore(grid, {
                       caller: caller,
                       condition: 'ved_veid=' + record.data.ved_veid
                   });
				   showMessage('提示', '更新成功!', 1000);
			   } else if(r.exceptionInfo){
				   showError(r.exceptionInfo);
			   } else{
				   saveFailure();
			   }
		   }
	   });
    }
});