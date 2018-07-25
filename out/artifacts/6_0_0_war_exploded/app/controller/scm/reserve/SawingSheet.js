Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.SawingSheet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.reserve.SawingSheet','core.grid.Panel2','core.grid.Panel5','core.toolbar.Toolbar','core.form.MultiField', 'core.form.FileField',
     		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
 			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
 			'core.button.Post', 'core.button.ResPost',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.form.YnField'      
 	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('ss_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: this.onGridItemClick
			},
			'erpGridPanel5': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('ss_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.beforeSave();
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ss_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('ss_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.beforeUpdate();
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addSawingSheet', '新增开料单', 'jsps/scm/reserve/sawingSheet.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ss_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ss_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ss_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ss_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ss_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ss_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ss_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ss_id').value);
    			}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ss_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('ss_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ss_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('ss_id').value);
    			}
    		},
    		'dbfindtrigger[name=ssb_batchcode]': {
  			   focus: function(t){
  				   t.setHideTrigger(false);
  				   t.setReadOnly(false);//用disable()可以，但enable()无效
  				   var record = Ext.getCmp('grid').selModel.lastSelected;
  				   var pr = record.data['ssb_prodcode'];
  				   if(pr == null || pr == ''){
  					   showError("请先选择料号!");
  					   t.setHideTrigger(true);
  					   t.setReadOnly(true);
  				   } else {
  					   var code = record.data['ssb_whcode'];
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
  		   }
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(){
		var me = this;
		var mm = me.FormUtil;
		var form = Ext.getCmp('form');
		if(! mm.checkForm()){
			return;
		}
		if(form.keyField){
 		   if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
 			   mm.getSeqId(form);
 		   }
 	   }
 	   var grids = Ext.ComponentQuery.query('gridpanel');
 	   var arg=new Array();
 	   if(grids.length > 0){
 		   for(var i=0;i<grids.length;i++){
 			   var param = me.GridUtil.getGridStore(grids[i]);
 			   if(grids[i].necessaryField.length > 0 && (param == null || param == '')){
 				   arg.push([]);
 			   } else {
 				   arg.push(param);
 			   }
 		   }
 		   me.onSave(arg[0],arg[1]);
 	   }else {
 		   me.onSave([]);
 	   }
    },
    onSave:function(param1,param2){
 	   var me = this;
 	   var form = Ext.getCmp('form');
 	   param1 = param1 == null ? [] : "[" + param1.toString() + "]";
 	   param2 = param2 == null ? [] : "[" + param2.toString() + "]";
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
 		   var reg = /[!@#$%^&*()'":,\/?]/;
 		   Ext.each(keys, function(k){
 			   f = form.down('#' + k);
 			   if(f && f.logic == 'ignore') {
 				   delete r[k];
 			   }
 			   //codeField值强制大写,自动过滤特殊字符
 			   if(k == form.codeField && !Ext.isEmpty(r[k])) {
 				   r[k] = r[k].trim().toUpperCase().replace(reg, '');
 			   }
 		   });
 		   if(!me.FormUtil.contains(form.saveUrl, '?caller=', true)){
 			   form.saveUrl = form.saveUrl + "?caller=" + caller;
 		   }
 		   me.FormUtil.save(r,param1,param2);
 	   }else{
 		   me.FormUtil.checkForm();
 	   }
    },
	beforeUpdate: function(){
		var me = this;
		var mm = me.FormUtil;
		var form = Ext.getCmp('form');
		if(! mm.checkForm()){
			return;
		}
		if(form.keyField){
 		   if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
 			   mm.getSeqId(form);
 		   }
 	   }
 	   var grids = Ext.ComponentQuery.query('gridpanel');
 	   var arg=new Array();
 	   if(grids.length > 0){
 		   for(var i=0;i<grids.length;i++){
 			   var param = me.GridUtil.getGridStore(grids[i]);
 			   if(grids[i].necessaryField.length > 0 && (param == null || param == '')){
 				   arg.push([]);
 			   } else {
 				   arg.push(param);
 			   }
 		   }
 		   me.onUpdate(arg[0],arg[1]);
 	   }else {
 		   me.onUpdate([]);
 	   }
    },
    onUpdate:function(param1,param2){
 	   var me = this;
 	   var form = Ext.getCmp('form');
 	   param1 = param1 == null ? [] : "[" + param1.toString() + "]";
 	   param2 = param2 == null ? [] : "[" + param2.toString() + "]";
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
 		   var reg = /[!@#$%^&*()'":,\/?]/;
 		   Ext.each(keys, function(k){
 			   f = form.down('#' + k);
 			   if(f && f.logic == 'ignore') {
 				   delete r[k];
 			   }
 			   //codeField值强制大写,自动过滤特殊字符
 			   if(k == form.codeField && !Ext.isEmpty(r[k])) {
 				   r[k] = r[k].trim().toUpperCase().replace(reg, '');
 			   }
 		   });
 		   if(!me.FormUtil.contains(form.updateUrl, '?caller=', true)){
 			   form.updateUrl = form.updateUrl + "?caller=" + caller;
 		   }
 		   me.FormUtil.update(r,param1,param2);
 	   }else{
 		   me.FormUtil.checkForm();
 	   }
    }
});