Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.VerifyApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.VerifyApply','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.TurnCheck','core.button.PrintBar',
      			'core.button.ClearSubpackage','core.button.Subpackage','core.button.ProduceBatch',
      			'core.button.GridWin','core.button.TurnPurcProdIO',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','core.button.PrintByCondition','core.button.Barcode','core.button.Split'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(record.data.vad_id > 0){
    					var btn = selModel.ownerCt.down('#erpEditSubpackageButton');
    					if(btn && !btn.hidden)
    						btn.setDisabled(false);
    					 btn = selModel.ownerCt.down('#erpSplitButton');
    					if(btn && !btn.hidden)
    						btn.setDisabled(false);
    					 btn = selModel.ownerCt.down('#erpUpdateDetailWHCode');
    					if(btn && !btn.hidden)
    						btn.setDisabled(false);    					
					}   								
					this.onGridItemClick(selModel, record);
    			}
    		},
    		'erpGridWinButton':{
    			afterrender: function(btn){
    				var id  = Ext.getCmp('va_id').value;
    				btn.setConfig({
    					text: '费用明细',
    					caller: 'ProdChargeDetail!AN',
    					condition: 'pd_anid=' + id,
    					paramConfig: {
    						pd_anid: id
    					}
    				});
    			},
    			beforesave : function(btn) {
    				var f = btn.ownerCt.ownerCt, p = f.down('field[name=va_turnstatuscode]');
    				if (p && 'TURNIN' == p.getValue()) {
    					Ext.Msg.alert("提示","该单据已入库,不能修改费用明细！");
    					return false;
    				}
    				return true;
    			}
    		},
    		/**
    		 * 明细分拆
    		 */
    		'erpSplitButton': {
    			beforerender: function(btn) { 
                   btn.text="明细拆分";
                   btn.width=100; 
                }, 
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
    			    var va_code=btn.ownerCt.ownerCt.ownerCt.items.items[0].items.items[0].value;
    				me.VerifyApplySplit(record,va_code);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var bool = true;
    				if(Ext.getCmp('va_date').value > new Date()){
    					bool = false;
    					showError('收料日期大于系统当前日期');return;
    				}
    				if(bool){
	    				var form = me.getForm(btn);
	    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	    					me.BaseUtil.getRandomNumber();//自动添加编号
	    				}
	    				//保存之前的一些前台的逻辑判定
	    				this.beforeSave();
    				}
    			}
    		},
    		'erpDeleteButton' : { 	
                click: {
                	lock: 2000, 
                	fn: function(btn) {
                		me.FormUtil.onDelete(Ext.getCmp('va_id').value);
                	}
                }
    		},
    		'#erpEditSubpackageButton': {
    			click: function(btn){
    				 var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
	    			 me.EditSubpackage(record);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addVerifyApply', '新增委外收料单', 'jsps/scm/purchase/verifyApply.jsp?whoami='+caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var bool = true;
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				if(Ext.getCmp('va_date').value > new Date()){
    					bool = false;
    					showError('收料日期大于系统当前日期');return;
    				}
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['vad_qty'] == null || item.data['vad_qty'] == '' || 
    								item.data['vad_qty'] == '0' || item.data['vad_qty'] == 0){
    							bool = false;
    							showError('明细表第' + item.data['vad_detno'] + '行的数量为空或0');return;
    						}
    					}
    				});
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('va_id').value);
    				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
					var reportName="VerifyApply";
					var condition='{VerifyApply.va_id}='+Ext.getCmp('va_id').value+'';
					var id=Ext.getCmp('va_id').value;
					me.FormUtil.onwindowsPrint2(id,reportName,condition);
    			}
    		},
    		'erpTurnPurcProdIOButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入采购验收单吗?", function(btn){
    					if(btn == 'yes'){
    						var id = Ext.getCmp('va_id').value;
    						me.FormUtil.setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/turnStorage.action',
    	    			   		params: {
    	    			   			id: id
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.setLoading(false);
    	    		         		var r = new Ext.decode(response.responseText);
    	    		         		if(r.exceptionInfo){
    	    		         			showError(r.exceptionInfo);
    	    		         		} else {
    									if(r.log) {
    										showMessage('提示', r.log);
    										var grid = Ext.getCmp('grid');
    										grid.GridUtil.loadNewStore(grid, {
    											caller: caller, 
    											condition: 'vad_vaid=' + id
    										});
    									}
    								}
    	    		         	}
    	    				});
    					}
    				});
    			}
    		},
    		'erpTurnCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: {
    				fn:function(btn){
        				me.turnQC();
        			},
        			lock:2000
    			}
    		},
    		'dbfindtrigger[name=vad_pucode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('va_vendcode')){
    					var code = Ext.getCmp('va_vendcode').value;
    					if(code == null || code == ''){
        					showError("请先选择供应商!");   
        					t.setHideTrigger(true);
        					t.setReadOnly(true);
        				} else {      
        					var obj = me.getBaseCondition();
        					if(obj.vendor){
        						t.dbBaseCondition = obj.vendor + "='" + code + "'";
        					}
        				}
    				}
    			}
    		},
    		'multidbfindtrigger[name=vad_pudetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['vad_pucode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var obj = me.getBaseCondition();
    					if(obj.field){
    						t.dbBaseCondition = obj.field + "='" + code + "'";
    					}
    				}
    			}
    		},
    		'dbfindtrigger[name=vad_pudetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['vad_pucode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var obj = me.getBaseCondition();
    					if(obj.field){
    						t.dbBaseCondition = obj.field + "='" + code + "'";
    					}
    				}
    			}
    		},
    		'erpProduceBatchButton':{
//    			afterrender:function(btn){
//    				var status=Ext.getCmp('va_statuscode');
//    				if(status && status.value !='ENTERING'){
//    					btn.hide();
//    				}
//    			},
    			click:function(btn){
    				var id=Ext.getCmp('va_id').value;
    				me.FormUtil.setLoading(true);//loading...
    				Ext.Ajax.request({
    			   		url : basePath + 'scm/purchase/ProduceBatch.action',
    			   		params: {
    			   			id:id
    			   		},
    			   		method : 'post',
    			   		callback: function(opt, s, r) {
    			   			me.FormUtil.setLoading(false);
    						var rs = Ext.decode(r.responseText);
    						if(rs.exceptionInfo) {
    							showError(rs.exceptionInfo);
    						} else {    						
    							showMessage('提示', '产生批号成功!',1000);
    							var grid=Ext.getCmp('grid');
    							var param={
    								caller:caller,
    								condition:gridCondition
    							};
    							me.GridUtil.loadNewStore(grid,param);
    						}
    					}
    				});
    			}
    		},
    		'erpSubpackageButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定分装?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/Subpackage.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('va_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback: function(opt, s, r) {
    	    			   			me.FormUtil.setLoading(false);
    	    						var rs = Ext.decode(r.responseText);
    	    						if(rs.exceptionInfo) {
    	    							showError(rs.exceptionInfo);
    	    						} else {
    	    							if(rs.log)
    	    								showMessage('提示', rs.log);
    	    						}
    	    					}
    	    				});
    					}
    				});
    			}
    		},
    		'erpClearSubpackageButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定清除分装?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/ClearSubpackage.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('va_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback: function(opt, s, r) {
    	    			   			me.FormUtil.setLoading(false);
    	    						var rs = Ext.decode(r.responseText);
    	    						if(rs.exceptionInfo) {
    	    							showError(rs.exceptionInfo);
    	    						} else {
    	    							if(rs.log)
    	    								showMessage('提示', rs.log);
    	    						}
    	    					}
    	    				});
    					}
    				});
    			}
    		},
    		'erpPrintBarButton':{
    			click: function(btn){
	    			var reportName = "bar_52";
					var condition = '{VerifyApplyDetailP.vadp_vacode}=' +"'"+ Ext.getCmp('va_code').value + "'";
					var id = Ext.getCmp('va_id').value;
					me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		  '#erpUpdateDetailWHCode':{
    			   click: function(btn) {
    				   var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
    				   this.UpdateDetailWHCode(record);
    			   }
    		   },
    	});
    }, 
    turnQC:function(){
		var me = this;
		var grid = Ext.getCmp('grid'),items = grid.store.data.items,data = new Array();
		var vaclass = Ext.getCmp('va_class').value,url="";
		if(vaclass == '采购收料单'){
			url = "scm/purchase/turnIQC.action";
		} else if(vaclass == '委外收料单'){
			url = "pm/make/turnFQC.action";
		}
		Ext.each(items, function(item){
			data.push({vad_id: item.data.vad_id});
		});
		grid.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + url,
	   		params: {
	   			data: Ext.encode(data)
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			grid.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   				return "";
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    				}
    				window.location.reload();
	   			}
	   		}
		});
	},
    /**
     *修改明细仓库
     * */
    UpdateDetailWHCode:function(record){
 	   var me = this;
 	   var win=Ext.create('Ext.window.Window', {
 		   width: 430,
 		   height: 250,
 		   closeAction: 'destroy',
 		   title: '<h1>修改仓库信息</h1>',
 		   layout: {
 			   type: 'vbox'
 		   },
 		   items: [{
 			   margin: '5 0 0 5',
 			   xtype: 'dbfindtrigger',
 			   fieldLabel: '仓库编号',
 			   name: 'whcode',
 			   value: record.data.vad_whcode,
 			   id:'whcode'
 		   },
 		   {
 			   margin: '5 0 0 5',
 			   xtype: 'textfield',
 			   fieldLabel: '仓库名称',
 			   name: 'whname',
 			   value: record.data.vad_whname,
 			   id:'whname'
 		   },{
 			  margin: '5 0 0 5',
               xtype:'checkbox',
               fieldLabel:'是否修改所有明细',
               labelWidth:120,
               name:'isalldetail',
               id:'isalldetail'
 		   }],
 		   buttonAlign: 'center',
 		   buttons: [{
 			   xtype: 'button',
 			   text: '保存',
 			   width: 60,
 			   iconCls: 'x-button-icon-save',
 			   handler: function(btn) {
 				   var w = btn.up('window');
 				   me.saveWhInfo(w);
 				   win.close();
 				   win.destroy();
 			   }
 		   },
 		   {
 			   xtype: 'button',
 			   columnWidth: 0.1,
 			   text: '关闭',
 			   width: 60,
 			   iconCls: 'x-button-icon-close',
 			   margin: '0 0 0 10',
 			   handler: function(btn) {
 				   var win = btn.up('window');
 				   win.close();
 				   win.destroy();
 			   }
 		   }]
 	   });
 	   win.show();
    },
    saveWhInfo:function(w){
 	   var whcode = w.down('field[name=whcode]').getValue();
 	   var isalldetail = w.down('field[name=isalldetail]').getValue();
 	   grid = Ext.getCmp('grid'),
 	   record = grid.getSelectionModel().getLastSelected();
 	   if (!whcode) {
 		   showError('请先设置仓库信息!');
 		   return;
 	   } else {
 		   var dd = {
 				   whcode:whcode,
 				   whname:w.down('field[name=whname]').getValue(),
 				   vad_id: record.data.vad_id,
 				   vad_vaid: record.data.vad_vaid,
 				   isalldetail:isalldetail
 				   };	    				 
 		   Ext.Ajax.request({
 			   url: basePath + 'scm/qc/updateWhCodeInfo.action',
 			   params: {
 				   data: unescape(Ext.JSON.encode(dd)),
 				   caller: caller
 			   },
 			   method: 'post',
 			   callback: function(opt, s, res) {
 				   var r = new Ext.decode(res.responseText);
 				   if (r.success) {
 					   grid.GridUtil.loadNewStore(grid, {
 						   caller: caller,
 						   condition: gridCondition
 					   });
 					   showMessage('提示', '更新成功!', 1000);
 				   } else if (r.exceptionInfo) {
 					   showError(r.exceptionInfo);
 				   } else {
 					   saveFailure();
 				   }
 			   }
 		   });
 	   }
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
	   this.GridUtil.onGridItemClick(selModel, record);
	},
	getBaseCondition: function(){
		var field = null;
		var vendor = '';
		switch (caller) {
			case 'VerifyApply': //采购收料单
				field = "pd_code";
				vendor="pu_vendcode";
				break;			
			case 'VerifyApply!OS': //委外收料单
				field = "mm_code";
				vendor="ma_vendcode";
				break;
		}
		var obj = new Object();
		obj.field = field;
		obj.vendor = vendor;
		return obj;
	},
	beforeSave: function(){
		var bool = true;
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['vad_qty'] == null || item.data['vad_qty'] == '' || 
						item.data['vad_qty'] == '0' || item.data['vad_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['vad_detno'] + '行的数量为空或0');return;
				}
				if(item.data['vad_unitpackage'] == null || item.data['vad_unitpackage'] == ''|| 
						item.data['vad_unitpackage'] == '0' || item.data['vad_unitpackage'] == 0){
					item.set('vad_unitpackage', item.data['vad_qty']);
				}
			}
		});
		if(bool){
			this.FormUtil.beforeSave(this);
		}
	},
	beforeUpdate: function(){
		var bool = true;
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
    	if(Ext.getCmp('va_date').value > new Date()){
    		bool = false;
    		showError('收料日期大于系统当前日期');return;
    	}
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['vad_qty'] == null || item.data['vad_qty'] == '' || 
						item.data['vad_qty'] == '0' || item.data['vad_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['vad_detno'] + '行的数量为空或0');return;
				}
				if(item.data['vad_unitpackage'] == null || item.data['vad_unitpackage'] == ''|| 
						item.data['vad_unitpackage'] == '0' || item.data['vad_unitpackage'] == 0){
					item.set('vad_unitpackage', item.data['vad_qty']);
				}
			}
		});
		if(bool){
			this.FormUtil.onUpdate(this);
		}
	},
	/**
	 *条码维护
	 **/
	EditSubpackage:function(record){
		var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
		   	height = Ext.isIE ? screen.height*0.75 : '100%';
		var vad_id = record.get('vad_id');
		var va_code = "'"+Ext.getCmp("va_code").value+"'";
		Ext.create('Ext.Window', {
			width: width,
			height: height,
			autoShow: true,
			layout: 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/scm/purchase/verifyApplyDetail.jsp?formCondition=vad_id=' 
					+ vad_id + ' and vad_code='+va_code+'&gridCondition=vadp_vadid=' + vad_id + ' and vadp_vacode='+va_code+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}]
		});
	},
	 /**
	 *收料单拆分
	 * */
	VerifyApplySplit:function(record,va_code){
		var me=this,originaldetno=Number(record.data.vad_detno);
		var puid=record.data.vad_vaid;
		var pdid=record.data.vad_id;
		Ext.create('Ext.window.Window',{
    		width:1010,
    		height:'95%',
    		iconCls:'x-grid-icon-partition',
    		title:'<h1>采购收料单拆分</h1>',
    		id:'win',
    		items:[{
    			xtype:'form',
    			layout:'column',
    			region:'north',
    			frame:true,
    			defaults:{
    				xtype:'textfield',
    				columnWidth:0.5,
    				readOnly:true,
    				fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;'
    			},
    			items:[{
    			 fieldLabel:'收料单号',
    			 value:va_code,
    			 id:'sacode'
    			},{
    			 fieldLabel:'物料编号'	,
    			 value:record.data.vad_prodcode
    			},{
    			 fieldLabel:'物料名称',
    			 value:record.data.pr_detail
    			},{
    			 fieldLabel:'原序号'	,
    			 value:record.data.vad_detno
    			},{
    		     fieldLabel:'原数量',
    		     value:record.data.vad_qty
    			},{
    			 fieldLabel:'规格',
    		     value:record.data.pr_spec
    			}],
    			buttonAlign:'center',
    			buttons:[{
    				xtype:'button',
    				columnWidth:0.12,
    				text:'保存',
    				width:60,
    				iconCls: 'x-button-icon-save',
    				margin:'0 0 0 30',
    				handler:function(btn){
    				   var store=Ext.getCmp('smallgrid').getStore();
    				   var count=0;
    				   var jsonData=new Array();
    				   var dd;
    				   Ext.Array.each(store.data.items,function(item){
    					  if(item.data.vad_qty>0){
    						  if(item.dirty){
    							  dd=new Object();
    							  //说明是新增批次
    							  dd['vad_qty']=item.data.vad_qty; 
    							  dd['vad_id']=item.data.vad_id;
    							  dd['vad_detno']=item.data.vad_detno;
    							  dd['vad_batchcode']=item.data.vad_batchcode;
    							  if(item.data.vad_whcode){
    								  dd['vad_whcode']=item.data.vad_whcode; 
    							  	  dd['vad_whname']=item.data.vad_whname; 
    							  }else{
    							  	 dd['vad_whcode']==null;
    							  	 dd['vad_whname']==null;
    							  }
    							  jsonData.push(Ext.JSON.encode(dd)); 
    						  }
    						  count+=Number(item.data.vad_qty);
    					  }
    				   });		   
    				   var assqty=Number(record.data.vad_qty);
    				   if(count!=assqty){
    					showError('分拆数量必须等于原数量!') ;  
    					return false;
    				   }else if(store.data.items[0].data['vad_qty']<record.data.vad_yqty){
    					showError('分拆后的数量不能小于已转数量!') ;  
       					return false; 
    				   }
    				   else{
    					   var r=new Object();
        				   r['vad_id']=record.data.vad_id;
        				   r['vad_vaid']=record.data.vad_vaid;
        				   r['vad_detno']=record.data.vad_detno;        
        				   var params=new Object();
        				   params.formdata = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
        					params.data = unescape(jsonData.toString().replace(/\\/g,"%"));
    					   Ext.Ajax.request({
    					   	  url : basePath +'scm/purchase/splitVerifyApply.action'/*basePath +'scm/purchase/splitPurchase.action'*/,
    					   	  params : params,
    					   	  waitMsg:'拆分中...',
    					   	  method : 'post',
    					   	  callback : function(options,success,response){
    					   		var localJson = new Ext.decode(response.responseText);
    					   		if(localJson.success){
    			    				saveSuccess(function(){
    			    					//add成功后刷新页面进入可编辑的页面 
    			    					Ext.getCmp('win').close();
    			    					 me.loadSplitData(originaldetno,puid,record); 
    			    				});
    				   			} else if(localJson.exceptionInfo){
    				   				var str = localJson.exceptionInfo;
    				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    				   					str = str.replace('AFTERSUCCESS', '');
    				   					saveSuccess(function(){
    				    					//add成功后刷新页面进入可编辑的页面 
    				   					 me.loadSplitData(originaldetno,puid,record); 
    				    				});
    				   					showError(str);
    				   				} else {
    				   					showError(str);
    					   				return;
    				   				}
    					   			
    					   	 } else{
    				   				saveFailure();
    				   			}
    					   	  }
    					   });
    					   
    				   }
    				}
    			},{
    				xtype:'button',
    				columnWidth:0.1,
    				text:'关闭',
    				width:60,
    				iconCls: 'x-button-icon-close',
    				margin:'0 0 0 10',
    				handler:function(btn){
    					Ext.getCmp('win').close();
    				}
    			}]
    		},{
    		  xtype:'gridpanel',
    		  region:'south',
    		  id:'smallgrid',
    		  layout:'fit',
    		  height:'80%',
    		  columnLines:true,
    		  store:Ext.create('Ext.data.Store',{
					fields:[{name:'vad_qty',type:'int'},{name:'vad_whcode',type:'string'},{name:'vad_whname',type:'string'},{name:'vad_batchcode',type:'string'},{name:'vad_id',type:'int'}],
				    data:[]
    		  }),
    		  plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
    		        clicksToEdit: 1,
    		        listeners:{
    		        	'edit':function(editor,e,Opts){
    		        		var record=e.record;
    		        		var version=record.data.ma_version;
    		        		if(version){
    		        			e.record.reject();
    		        		 Ext.Msg.alert('提示','不能修改已拆分明细!');
    		        		}
    		        		}
    		        	}
    		    })],
    		  tbar: [{
    			    tooltip: '添加批次',
    	            iconCls: 'x-button-icon-add',
    	            width:25,
    	            handler : function() {
    	            	var store = Ext.getCmp('smallgrid').getStore();
    	                var r = new Object();
    	                r.vad_qty=0; 
    	                r.vad_id=0;
    	                r.vad_detno=store.getCount()+1;
    	                /*r.vad_whcode=record.get('vad_whcode');
    	                r.vad_whname=record.get('vad_whname');*/
    	                store.insert(store.getCount(), r);
    	            }
    	        }, {
    	            tooltip: '删除批次',
    	            width:25,
    	            itemId: 'delete',
    	            iconCls: 'x-button-icon-delete',
    	            handler: function(btn) {
    	                var sm = Ext.getCmp('smallgrid').getSelectionModel();
    	                var record=sm.getSelection();
    	                var pd_id=record[0].data.pd_id;
    	                if(pd_id&&pd_id!=0){
    	                	Ext.Msg.alert('提示','不能删除已拆批次或原始行号!');
    	                	return;
    	                }
    	                var store=Ext.getCmp('smallgrid').getStore();
    	                store.remove(record);
    	                if (store.getCount() > 0) {
    	                    sm.select(0);
    	                }
    	            },
    	            disabled: true
    	        }],
    	      listeners:{
    	    	  itemmousedown:function(selmodel, record){
    	    		  selmodel.ownerCt.down('#delete').setDisabled(false);
    	    	  }
    	      }, 
    	      dbfinds: [{dbGridField: "wh_code", field: "vad_whcode", trigger: null},
    	      			{dbGridField: "wh_description", field: "vad_whname", trigger: null}],
    		  columns:[{
    			 dataIndex:'vad_detno',
    			 header:'序号',
    			 format:'0',
    			 xtype:'numbercolumn',
    			 width:40
    		   },{
    			  dataIndex:'vad_qty',
    			  header:'数量',
    			  width:80,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  renderer:function(val,meta,record){
   				   if(record.data.ma_version){
   					  meta.tdCls = "x-grid-cell-renderer-cl";
   				   }
   				   return val;
   			     },
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  },{
    			  dataIndex:'vad_id',
    			  header:'vadid',
    			  width:0,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  },{
    		  	dataIndex:'vad_whcode',
    		  	header:'仓库编号',
    		  	renderer:function(val,meta,record){
     				   if(record.data.originaldetno){
     					  meta.tdCls = "x-grid-cell-renderer-cl";
     				   }
     				  return val;
     			} ,
     			 editor:{
	   				  xtype:'dbfindtrigger',
	   				  dbfind: "WareHouse|wh_code"
	   			  },
     			editable:true
    		  },{
    		  	dataIndex:'vad_whname',
    		  	header:'仓库名称'
    		  },{
    			dataIndex:'vad_batchcode',
    			header:'批号', 
    			width:100,
    			renderer:function(val,meta,record){
     				   if(record.data.originaldetno){
     					  meta.tdCls = "x-grid-cell-renderer-cl";
     				   }
     				  return val;
     			} ,
     			 editor:{
	   				  xtype:'textfield',
	   				  format:'0',
	   				  hideTrigger: true
	   			  },
     			editable:true
    		  }/*,{
    			dataIndex:'pd_qtyreply',
    			header:'回复数量',
    			 width:80,
	   			  xtype:'numbercolumn',
	   			  editable:true,
	   			  renderer:function(val,meta,record){
	  				   if(record.data.ma_version){
	  					  meta.tdCls = "x-grid-cell-renderer-cl";
	  				   }
	  				   return val;
	  			     },
	   			  editor:{
	   				  xtype:'numberfield',
	   				  format:'0',
	   				  hideTrigger: true
	   			  }
    		  },{
    			dataIndex:'pd_isok',
    			header:'是否准时',
    			xtype:'yncolumn',
    			width:80,
    			editable:true
    		  },{
    			dataIndex:'pd_replydetail',
    			header:'回复明细', 
    			width:100,
    			renderer:function(val,meta,record){
     				   if(record.data.originaldetno){
     					  meta.tdCls = "x-grid-cell-renderer-cl";
     				   }
     				  return val;
     			} ,
     			 editor:{
	   				  xtype:'textfield',
	   				  format:'0',
	   				  hideTrigger: true
	   			  },
     			editable:true
    		  },{
    			dataIndex:'pd_yqty',
    			header:'已转收料数',
    			xtype:'numbercolumn',
    			width:100,
    			editable:false
    		  },{
    			 dataIndex:'pd_acceptqty',
      			header:'已转验收数',
      			xtype:'numbercolumn',
      			width:100,
      			editable:false  
    		  },{
    			  dataIndex:'pd_id',
    			  header:'pdid',
    			  width:0,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  },{
    		  	dataIndex:'pd_sellercode',
    		  	header:'业务员编号',
    		  	renderer:function(val,meta,record){
     				   if(record.data.originaldetno){
     					  meta.tdCls = "x-grid-cell-renderer-cl";
     				   }
     				  return val;
     			} ,
     			 editor:{
	   				  xtype:'dbfindtrigger',
	   				  dbfind: "Employee|em_code"
	   			  },
     			editable:true
    		  },{
    		  	dataIndex:'pd_seller',
    		  	header:'业务员'
    		  }*/]
    		}] ,
    	listeners:{
		    'beforeclose':function(view ,opt){
		    	var grid = Ext.getCmp('grid');
				var value = Ext.getCmp('va_id').value;
				var gridCondition = grid.mainField + '=' + value,
				gridParam = {caller: caller, condition: gridCondition};
				me.GridUtil.loadNewStore(grid, gridParam);  
		    }
		  }
    	}).show();
         this.loadSplitData(originaldetno,puid,record);
	},
	loadSplitData:function(detno,puid,record){
		 var grid=Ext.getCmp('smallgrid');
        grid.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params:{
        	  caller:'VerifyApplySplit',
        	  condition:"vad_detno="+detno+" AND vad_vaid="+puid+" order by vad_id asc"
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data; 
        		 grid.store.loadData(data); 
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		grid.fireEvent('storeloaded', grid, data);
        	}
        });
	}
});