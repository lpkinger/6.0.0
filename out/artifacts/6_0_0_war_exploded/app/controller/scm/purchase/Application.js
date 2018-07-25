Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.Application', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.button.PrintByCondition','core.form.Panel','scm.purchase.Application','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit','core.button.Modify','core.button.CopyByConfigs',
				'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.UpdateForNeedData','core.button.DeleteDetail','core.button.ResSubmit',
				'core.button.TurnPurc','core.button.Flow','core.button.ImportExcel', 'core.button.UpdateRemark','core.button.ProduceBatch','core.trigger.MultiDbfindTrigger',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.form.FileField','core.button.MRPResourceScan','core.button.TDMaterialSearch'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'field[name=ap_remark]':{
				afterrender: function(field){
					Ext.defer(function(){
						field.setReadOnly(false);
					}, 200);
				}
			},
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(record.data.ad_prodcode != null && record.data.ad_prodcode != ''){
						var btn = Ext.getCmp('erpMRPResourceScan_button');
						if(btn){
							btn.setDisabled(false);
							btn._ad_prodcode = record.data.ad_prodcode;
						}
					}   								
					this.onGridItemClick(selModel, record);
    			
    			}
    		},
    		'#erpTDMaterialSearch' :{
    			beforerender:function(btn){
    				var ap_refcode = Ext.getCmp('ap_refcode').getValue();
    				if(Ext.isEmpty(ap_refcode)){
    					btn.hide();
    				}else{
    					btn.setDisabled(false);
    				}
    			},
    			click:function(btn){
    				var refcode = Ext.getCmp('ap_refcode').getValue();
    				var ap_id = Ext.getCmp('ap_id').getValue();
    				var win = new Ext.window.Window({
    					id : 'win_Material',
	 					height : '90%',
	 					width : '95%',
	 					maximizable : true,
	 					buttonAlign : 'center',
	 					layout : 'anchor',
	 					items : [ {
	 						tag : 'iframe',
	 						frame : true,
	 						anchor : '100% 100%',
	 						layout : 'fit',
	 						html : '<iframe id="iframe_win_Material'+refcode+'" src="'+basePath+'jsps/common/datalist.jsp?_noc=1&whoami=TDMaterialSearch&urlcondition=ar_apid=\''+ap_id+'\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
	 					} ]
	 				});
	 				win.show();
    			}
    		},
    		'#erpMRPResourceScan_button':{
    			click:function(btn){
    				var refcode = Ext.getCmp('ap_refcode').getValue();
    				var prodcode = btn._ad_prodcode;
    				if(Ext.isEmpty(refcode)){
    					showError("[MRP单号]为空,不能查看MRP来源！");
    					return;
    				}
    				if (!Ext.isEmpty(refcode)&&!Ext.isEmpty(prodcode)){
	    				var win = new Ext.window.Window({  
	    					id : 'win_Resource',
		 					height : '90%',
		 					width : '95%',
		 					maximizable : true,
		 					buttonAlign : 'center',
		 					layout : 'anchor',
		 					items : [ {
		 						tag : 'iframe',
		 						frame : true,
		 						anchor : '100% 100%',
		 						layout : 'fit',
		 						html : '<iframe id="iframe_MRPResourceScanWin'+refcode+'" src="'+basePath+'jsps/common/datalist.jsp?_noc=1&whoami=Desk!MrpResultDetail&urlcondition=md_mrpcode=\''+refcode+'\' and md_prodcode=\''+prodcode+'\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
		 					} ]
		 				});
		 				win.show();
    				}
    			}
    		},
    		'field[name=ap_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ap_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
					if(codeField.value == null || codeField.value == ''){
						me.BaseUtil.getRandomNumber(caller);//自动添加编号
						var res = me.getLeadCode(Ext.getCmp('ap_kind').value);
						if(res != null && res != ''){
							codeField.setValue(res + codeField.getValue());
						}
					}
    				var bool = true;
    				var grid = Ext.getCmp('grid');
    				//数量不能为空或0
    				//给从表赋值:vendcode、vendname
    				var firstItem = grid.store.getAt(0);
    				items = grid.store.data.items,
    				recorddate = Ext.getCmp('ap_recorddate').value;
    				Ext.each(items, function(item){
    					//当点击保存或者更新按钮时，默认把明细行第一行的项目编号和项目名称赋值给后面带编号的明细行
    					if(firstItem.get('ad_prodcode')&&firstItem.get('ad_prjcode')&&firstItem.get('ad_prjname')) {
        		    		var ad_prjcode = firstItem.get('ad_prjcode');
        		    		var ad_prjname = firstItem.get('ad_prjname');
    	    		    	if(!Ext.isEmpty(item.get('ad_prodcode'))){
    	    		    		if(Ext.isEmpty(item.get('ad_prjcode'))) {
    		    		    		/*item.set('ad_prjcode', ad_prjcode);*/
    	    		    			item.data['ad_prjcode']=ad_prjcode;
    	    		    			item.dirty=true;
    	    		    		}
    		    		    	if(Ext.isEmpty(item.get('ad_prjname'))) {
    		    		    		/*item.set('ad_prjname', ad_prjname);*/
    		    		    		item.data['ad_prjname']=ad_prjname;
    		    		    		item.dirty=true;
    		    		    	}
    	    		    	} 
        		    	}
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['ad_qty'] == null || item.data['ad_qty'] == '' || item.data['ad_qty'] == '0'
    							|| item.data['pd_qty'] == 0){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的数量为空');
    							/*return;*/
    							return false;
    						}
    						if(Ext.isEmpty(item.data['ad_delivery'])){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的需求日期不能为空');
    							return false;
    						}
    						if(Ext.Date.format(item.data['ad_delivery'],'Y-m-d') < Ext.Date.format(recorddate,'Y-m-d')){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的需求日期小于单据录入日期');
    							return false;
    						}
    						if(Ext.Date.format(item.data['ad_delivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的需求日期小于单据当前日期');
    							return false;
    						}
    					}
    				});
    				if(bool){
    					this.FormUtil.beforeSave(this);
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpProduceBatchButton':{
				beforerender:function(btn){					
    				btn.setText("计  算");    				
    			},
				click: function(btn){
					var id=Ext.getCmp('ap_id').value;
					grid = Ext.getCmp('grid');
					Ext.Ajax.request({
						url : basePath + "scm/purchase/applicationdataupdate.action",
						params: {
							id:id
						},
						method : 'post',
						async: false,
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);
								return;
							}
							grid.GridUtil.loadNewStore(grid,{
								 caller:'Application',
								 condition:gridCondition,
								 _noc:1
								});
							showError("计算成功！");							
						}
					});
				}
			},
    		 'erpImportExcelButton':{
  			   afterrender:function(btn){
  				   var statuscode=Ext.getCmp('ap_statuscode').getValue();
  				   if(statuscode&&statuscode!='ENTERING'){
  					   btn.hide();
  				   }
  			   }  
  		   },
  		 'filefield[id=excelfile]':{
			   change: function(field){
					var filename = '';
			    	if(contains(field.value, "\\", true)){
			    		filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
			    	} else {
			    		filename = field.value.substring(field.value.lastIndexOf('/') + 1);
			    	}
					field.ownerCt.getForm().submit({
	            	    url: basePath + 'common/upload.action?em_code=' + em_code,
	            		waitMsg: "正在解析文件信息",
	            		success: function(fp,o){
	            			if(o.result.error){
	            				showError(o.result.error);
	            			} else {	            				
	            				var filePath=o.result.filepath;	
	            				var keyValue=Ext.getCmp('ap_id').getValue();
	            				Ext.Ajax.request({//拿到form的items
	            		        	url : basePath + 'scm/application/ImportExcel.action',
	            		        	params:{
		            					  id:keyValue,
		            					  fileId:filePath
		            				  },
	            		        	method : 'post',
	            		        	callback : function(options,success,response){
	            		        		var result=Ext.decode(response.responseText);
	            		        		if(result.success){
	            		        			var grid=Ext.getCmp('grid');
	            		        			var param={
	            		        				caller:'Application',
	            		        				condition:'ad_apid='+keyValue
	            		        			};
	            		        			grid.GridUtil.loadNewStore(grid,param);
	            		        		}else{
	            		        			if(result.exceptionInfo != null){
	            		            			showError(res.exceptionInfo);return;
	            		            		}
	            		        		}
	            		        	}
	            				});	            				
	            			}
	            		}	
	            	});
				}
		   },
		   //hey S 为请购单添加更新需求日期按钮
		   '#ap_xqrq_user' : {
   				beforerender : function(f){
   					var status = Ext.getCmp('ap_statuscode');
   					if(status.value != 'COMMITED'){
   						f.readOnly=false;
   						f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
   					}
   				}
   			},
   			'erpUpdateNeedDataButton': {
   				afterrender:function(btn){
					var status = Ext.getCmp('ap_statuscode');
    				if(status.value != 'COMMITED'){
    					btn.show();
    				}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
   			},
   			//hey E 为请购单添加更新需求日期按钮
    		'erpUpdateButton': {   			
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var bool = true;
    				//数量不能为空或0
    				//给从表赋值:vendcode、vendname
    				  var grid = Ext.getCmp('grid');
    				  var items = grid.store.data.items;
    				  var firstItem = grid.store.getAt(0);
    				  var c = Ext.getCmp('ap_code').value;
    				  var recorddate = Ext.getCmp('ap_recorddate').value;
    				  Ext.each(items, function(item){
    					//把明细行第一行的项目名称和项目编号赋值给下面带编号的行
    					if(firstItem.get('ad_prjcode')&&firstItem.get('ad_prjname')) {
        		    		var ad_prjcode = firstItem.get('ad_prjcode');
        		    		var ad_prjname = firstItem.get('ad_prjname');
    	    		    	if(!Ext.isEmpty(item.get('ad_prodcode'))){
    	    		    		if(Ext.isEmpty(item.get('ad_prjcode'))) {
    		    		    		/*item.set('ad_prjcode', ad_prjcode);*/
    	    		    			item.data['ad_prjcode']=ad_prjcode;
    	    		    			item.dirty=true;
    	    		    		}
    		    		    	if(Ext.isEmpty(item.get('ad_prjname'))) {
    		    		    		/*item.set('ad_prjname', ad_prjname);*/
    		    		    		item.data['ad_prjname']=ad_prjname;
    		    		    		item.dirty=true;
    		    		    	}
    	    		    	} 
        		    	}
    					//item.set('ad_code',c);
    					 item['ad_code']=c;
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['ad_qty'] == null || item.data['ad_qty'] == '' || item.data['ad_qty'] == '0'
    							|| item.data['pd_qty'] == 0){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的数量为空');
    							return false;
    						}
    						if(Ext.isEmpty(item.data['ad_delivery'])){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的需求日期不能为空');
    							return false;
    						}
    						if(Ext.Date.format(item.data['ad_delivery'],'Y-m-d') < Ext.Date.format(recorddate,'Y-m-d')){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的需求日期小于单据录入日期');
    							return false;
    						}
    						if(Ext.Date.format(item.data['ad_delivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的需求日期小于单据当前日期');
    							return false;
    						}
    					}
    				});
    				if(bool){
    					this.FormUtil.onUpdate(this);
    				}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addApplication', '新增请购单', 'jsps/scm/purchase/application.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), c = Ext.getCmp('ap_code').value,
    				    items = grid.store.data.items, recorddate = Ext.getCmp('ap_recorddate').value;
    				var bool = true;
    				Ext.each(items, function(item){
    					item['ad_code']=c;
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['ad_qty'] == null || item.data['ad_qty'] == '' || item.data['ad_qty'] == '0'
    							|| item.data['pd_qty'] == 0){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的数量为空');return;
    						}
    						if(Ext.isEmpty(item.data['ad_delivery'])){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的需求日期不能为空');return;
    						}
    						if(Ext.Date.format(item.data['ad_delivery'],'Y-m-d') < Ext.Date.format(recorddate,'Y-m-d')){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的需求日期小于单据录入日期');return;
    						}
    						if(Ext.Date.format(item.data['ad_delivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
    							bool = false;
    							showError('明细表第' + item.data['ad_detno'] + '行的需求日期小于单据当前日期');return;
    						}
    					}
    				});
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('ap_id').value);
    				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
				var reportName="application";
				var condition='{Application.ap_id}='+Ext.getCmp('ap_id').value+'';
				var id=Ext.getCmp('ap_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
    		},
    		'erpUpdateRemarkButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_statuscode');
    				if (status && status.value == 'ENTERING') {
    					btn.hide();
    				}
    			},
    			click : function(btn) {
    				Ext.Ajax.request({
    					url : basePath + 'common/updateByCondition.action',
    					params : {
    						caller : caller,
    						table : 'Application',
    						update : 'ap_remark=\'' + Ext.getCmp('ap_remark').value + '\'',
    						condition : 'ap_id=' + Ext.getCmp('ap_id').value
    					},
    					callback : function(opt, s, res) {
    						var r = Ext.decode(res.responseText);
    						if (r.success) {
    							alert('修改成功!');
    						}
    					}
    				});
    			}
    		},
    		/*'field[name=ad_qty]':{
    			change: function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var value=record.data['ad_qty'];
    				if(!record.data['ad_minorder'] || record.data['ad_minorder']==null){
    					showError('最小订购量为空,请先填写最小订购量');
    					return;
    				}
    				if(!record.data['ad_minpack'] || record.data['ad_minpack']==null){
    					showError('最小包装量为空,请先填写最小包装量');
    					return;
    				}
    				if(value<record.data['ad_minorder']){
    					showError('需求数量小于最小订购量，请重新填写！');
    					record.set('ad_minorder',null);
    					return;
    				}
    				if(value/record.data['ad_minpack'] !=0){
    					showError('需求数量不是最小包装量的整数倍，请重新填写！');
    					record.set('ad_minorder',null);
    					return;
    				}
    			}
    			
    		},*/
    		'erp2PurcButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_turnstatuscode');
    				if(status && status.value != 'PARTPURC'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入采购单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/turnPurchase.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('ap_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					window.location.reload();
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'#erpUpdateQtyButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_statuscode');
    				if(status && (status.value != 'AUDITED' && status.value != 'FINISH')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    				me.updateQty(record);
    			}
    		},
            'button[id=Voucher]':{
            	afterrender:function(btn){
            		var btn = Ext.getCmp('Voucher');
            		btn.hide();
            	}
            }
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    	var btn = Ext.getCmp('erpUpdateQtyButton');
		if(btn && record.data.ap_statuscode != 'AUDITED'){
			btn && btn.setDisabled(false);
		}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getLeadCode: function(type) {
		var result = null;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'PurchaseKind',
	   			field: 'pk_excode',
	   			condition: 'pk_name=\'' + type + '\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				result = r.data;
	   			}
	   		}
		});
		return result;
	},
	updateQty:function(record){
  	   var win = this.updateQtywindow;
  	   if (!win) {
  		   this.updateQtywindow = win = this.getUpdateQtyWindow(record);
  	   }
  	   win.show();
	},
    getUpdateQtyWindow : function(record) {
    	var me = this;
    	var win = Ext.create('Ext.window.Window',{
  		   	width: 330,
  		   	height: 160,
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
  			   	fieldLabel:'请购数量',
  			   	value: record.data.ad_qty,
  			   	name:'ad_qty',
  			   	id:'ad_qty'
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
    	return win;
    },
    saveQty: function(w) {
    	var ad_qty = w.down('field[name=ad_qty]').getValue(),
    	grid = Ext.getCmp('grid'),
    	record = grid.getSelectionModel().getLastSelected();
    	var dd = {
   			ad_id : record.data.ad_id,
   			ad_qty : ad_qty,
   			ad_yqty : record.data.ad_yqty,
   			ad_detno : record.data.ad_detno,
   			ad_oldqty : record.data.ad_qty,
   			caller: caller 
   	   };
   	   Ext.Ajax.request({
   			url : basePath +'scm/purchase/updateQty.action',
   			params : {
   				_noc: 1,
   				data: unescape(Ext.JSON.encode(dd))
   			},
   			method : 'post',
   			callback : function(opt, s, res){
   				var r = new Ext.decode(res.responseText);
   				if(r.success){
   					window.location.reload();
   					showError("更新成功！");
   				} else if (r.exceptionInfo){
   					showError(r.exceptionInfo);
   				}
   			}
   	   	});
    }
});