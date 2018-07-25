Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ProdInOutApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.reserve.ProdInOutApply','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.button.TurnProdOut',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: function(selModel, record){
					if(caller == 'ProdInOutApply!CGYT'){
						if(record.data.pd_id > 0 && Ext.isEmpty(record.data.pd_ordercode)){
							var btn = selModel.ownerCt.down('#erpGetPurcPrice');
							if(btn && !btn.hidden)
								btn.setDisabled(false);
						}  
					}
				},
				itemclick:this.onGridItemClick
			},
			'#erpGetPurcPrice': {
 			   click: function(btn){
 				   var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
 				   me.getPurcPrice(record);
 			   }
 		   },
			'erpSaveButton': {
				click: function(btn){	
					var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					this.FormUtil.beforeSave(this);
				}
			},
			'field[name=pi_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pi_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
			'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pi_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addProdInOutApply', '新增验退申请单', 'jsps/scm/reserve/prodInOutApply.jsp?whoami=' + caller);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pi_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pi_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pi_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pi_id').value);
				}
			},
			'dbfindtrigger[name=pd_ordercode]': {
 			   focus: function(t){
 				   t.setHideTrigger(false);
 				   t.setReadOnly(false);
 				   if(Ext.getCmp('pi_cardcode')){
 					   var code = Ext.getCmp('pi_cardcode').value;
 					   if(code != null && code != ''){
 						   var obj = me.getCodeCondition();
 						   if(obj && obj.field){
 							   t.dbBaseCondition = obj.field + "='" + code + "'";
 						   }
 					   }
 				   }
 				   if(caller=='ProdInOut!OutReturn'){//借货归还单
 					   var code = Ext.getCmp('pi_cardcode').value;
 					   if(code != null && code != ''){
 						   if(t.dbBaseCondition==null||t.dbBaseCondition==''){
 							   t.dbBaseCondition= "pi_cardcode='"+code+"'";
 						   }else{
 							   t.dbBaseCondition=t.dbBaseCondition+" and pi_cardcode='"+code+"'";
 						   }
 					   }
 				   }
 			   },
 			   aftertrigger: function(t){
 				   if(Ext.getCmp('pi_cardcode')){
 					   var obj = me.getCodeCondition();
 					   if(obj && obj.fields){
 						   me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
 					   }
 				   }
 			   }
 		   },
 		   'dbfindtrigger[name=pd_orderdetno]': {
 			   focus: function(t){
 				   t.setHideTrigger(false);
 				   t.setReadOnly(false);//用disable()可以，但enable()无效
 				   var record = Ext.getCmp('grid').selModel.lastSelected;
 				   var code = record.data['pd_ordercode'];
 				   if(code == null || code == ''){
 					   showError("请先选择关联单号!");
 					   t.setHideTrigger(true);
 					   t.setReadOnly(true);
 				   } else {
 					   var field = me.getBaseCondition();
 					   if(field){
 						   t.dbBaseCondition = field + "='" + code + "'";
 					   }
 				   }
 			   }
 		   },
 		   'dbfindtrigger[name=pd_whcode]':{
 			   aftertrigger: function(t){
 				   if(caller=='ProdInOut!AppropriationOut'||caller=='ProdInOut!Sale'||caller=='ProdInOut!AppropriationIn'){
 					   var inwhcode=t.value;
 					   var record = Ext.getCmp('grid').selModel.lastSelected;
 					   var prodcode=record.data['pd_prodcode'];
 					   if(prodcode&&inwhcode){
 						   var obj = {tablename:'productWH',fields:'pw_onhand'};
 						   me.FormUtil.getFieldsValue(obj.tablename, obj.fields,"pw_whcode='"+inwhcode+"' AND pw_prodcode='"+prodcode+"'" , "pd_inqty",record);
 					   }
 				   }
 			   }
 		   },
 		   'multidbfindtrigger[name=pd_orderdetno]': {
 			   focus: function(t){
 				   t.setHideTrigger(false);
 				   t.setReadOnly(false);//用disable()可以，但enable()无效
 				   var record = Ext.getCmp('grid').selModel.lastSelected;
 				   var code = record.data['pd_ordercode'];
 				   if(code == null || code == ''){
 					   showError("请先选择关联单号!");
 					   t.setHideTrigger(true);
 					   t.setReadOnly(true);
 				   } else {
 					   var field = me.getBaseCondition();
 					   if(field){
 						   t.dbBaseCondition = field + "='" + code + "'";
 					   }
 				   }
 			   }
 		   },
 		   'dbfindtrigger[name=pd_batchcode]': {
 			   focus: function(t){
 				   t.setHideTrigger(false);
 				   t.setReadOnly(false);//用disable()可以，但enable()无效
 				   var record = Ext.getCmp('grid').selModel.lastSelected;
 				   var pr = record.data['pd_prodcode'];
 				   if(pr == null || pr == ''){
 					   showError("请先选择料号!");
 					   t.setHideTrigger(true);
 					   t.setReadOnly(true);
 				   } else {
 					   var code = record.data['pd_whcode'];
 					   if(code == null || code == ''){
 						   if(Ext.getCmp('pi_whcode')) {
 							   code = Ext.getCmp('pi_whcode').value;
 							   if(code == null || code == ''){
 								   showError("请先选择仓库!");
 								   t.setHideTrigger(true);
 								   t.setReadOnly(true);
 							   } else {
 								   t.dbBaseCondition = "ba_whcode='" + code + "' AND ba_prodcode='" + pr + "'";
 							   }
 						   } else {
 							   t.dbBaseCondition = "ba_prodcode='" + pr + "'";
 						   }
 					   } else {
 						   t.dbBaseCondition = "ba_whcode='" + code + "' AND ba_prodcode='" + pr + "'";
 					   }
 				   }
 			   }
 		   },
		   'erpTurnProdOutButton':{
			   afterrender: function(btn){
				   btn.setText('转验退单');
				   var status = Ext.getCmp("pi_statuscode");
				   if(status && status.value != 'AUDITED'){
					   btn.hide();
				   }
			   },
			   click: function(m){
				   //转采购验退单
				   if(caller == 'ProdInOutApply!CGYT'){
					   me.batchdeal('ProdInOutApply!ToProdPurcOut!Deal', ' pd_piid=' + Ext.getCmp('pi_id').value +' and nvl(pd_yqty,0) < nvl(pd_outqty,0)', 'scm/reserve/applyTurnProdIO.action?type=ProdInOut!PurcCheckout');
				   }
				   //转委外验退单
				   if(caller == 'ProdInOutApply!WWYT'){
					   me.batchdeal('ProdInOutApply!ToProdOSOut!Deal', ' pd_piid=' + Ext.getCmp('pi_id').value +' and nvl(pd_yqty,0) < nvl(pd_outqty,0)', 'scm/reserve/applyTurnProdIO.action?type=ProdInOut!OutesideCheckReturn');

				   }
			   }
		   }
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	batchdeal: function(nCaller, condition, url){
 	   var win = new Ext.window.Window({
 		   id : 'win',
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
 			   html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
 			   + "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
 		   }],
 		   buttons : [{
 			   name: 'confirm',
 			   text : $I18N.common.button.erpConfirmButton,
 			   iconCls: 'x-button-icon-confirm',
 			   cls: 'x-btn-gray',
 			   listeners: {
 				   buffer: 500,
 				   click: function(btn) {
 					   var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
 					   btn.setDisabled(true);
 					   grid.updateAction(url);
 				   }
 			   }
 		   }, {
 			   text : $I18N.common.button.erpCloseButton,
 			   iconCls: 'x-button-icon-close',
 			   cls: 'x-btn-gray',
 			   handler : function(){
 				   Ext.getCmp('win').close();
 			   }
 		   }]
 	   });
 	   win.show();
    },
	 getBaseCondition: function(){
  	   var field = null;
  	   switch (caller) {
  	   case 'ProdInOutApply!CGYT': //采购验退申请单
  		   field = "pd_code";break;
  	   case 'ProdInOutApply!WWYT': //委外验退申请单
  		   field = "mm_code";break;
  	   }
  	   return field;
     },
     getCodeCondition: function(){
  	   var field = null;
  	   var fields = '';
  	   var tablename = '';
  	   var myfield = '';
  	   var tFields = '';
  	   switch (caller) {
  	   case 'ProdInOutApply!CGYT': //采购验退申请单
  		   field = "pu_vendcode";
  		   tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_paymentcode,pi_transport,pi_paydate,pi_receivecode,pi_receivename';
  		   fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_paymentscode,pu_transport,pu_suredate,pu_receivecode,pu_receivename';
  		   tablename = 'Purchase';
  		   myfield = 'pu_code';
  		   break;
  	   case 'ProdInOutApply!WWYT': //委外验退申请单
  		   field = "ma_vendcode";
  		   tFields = 'pi_cardcode,pi_title,pi_departmentcode,pi_departmentname,pi_currency,pi_rate,pi_receivecode,pi_receivename';
  		   fields = 'ma_vendcode,ma_vendname,ma_departmentcode,ma_departmentname,ma_currency,ma_rate,ma_apvendcode,ma_apvendname';
  		   tablename = 'Make';
  		   myfield = 'ma_code';
  		   break;
  	   }
  	   var obj = new Object();
  	   obj.field = field;
  	   obj.fields = fields;
  	   obj.tFields = tFields;
  	   obj.tablename = tablename;
  	   obj.myfield = myfield;
  	   return obj;
     },
     getPurcPrice:function(record){
  	   warnMsg("确定要获取采购单价吗?", function(btn){
				if(btn == 'yes'){
		    	   var pdid = record.data.pd_id, prod = record.data.pd_prodcode, piid = record.data.pd_piid,
		    	   	   vend = Ext.getCmp('pi_cardcode').value, curr = Ext.getCmp('pi_currency').value,
		    	   	   grid = Ext.getCmp('grid');
		    	   if(Ext.isEmpty(vend)){
		    		   showError('请先选择供应商！');
		    		   return;
		    	   }
		    	   if(Ext.isEmpty(curr)){
		    		   showError('请先选择币别！');
		    		   return;
		    	   }
		    	   if(Ext.isEmpty(prod)){
		    		   showError('请先选择物料！');
		    		   return;
		    	   }
	    		   Ext.Ajax.request({
	    			   url : basePath +'scm/reserve/getPrice.action',
	    			   params : {
	    				   pdid : pdid,
	    				   piid : piid,
	    				   caller: caller 
	    			   },
	    			   method : 'post',
	    			   callback : function(opt, s, res){
	    				   var r = new Ext.decode(res.responseText);
	    				   if(r.success){
	    					   grid.GridUtil.loadNewStore(grid, {
	                               caller: caller,
	                               condition: 'pd_piid=' + piid
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
     }
});