Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.MultiForm', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil'],
    views:[
   		'crm.marketmgr.marketresearch.MultiForm','crm.marketmgr.marketresearch.MyForm','ma.MyGrid','ma.MyDetail','core.button.DeleteDetail','core.toolbar.Toolbar',
   		'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.grid.TfColumn','core.grid.YnColumn',
   		'core.button.UUListener', 'core.button.DbfindButton','core.button.ComboButton', 'core.form.YnField','core.form.FileField',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
   	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	this.control({ 
    		'multidbfindtrigger': {
    			render: function(field){
    				if(field.name == 'fo_button4add' || field.name == 'fo_button4rw'){
    					var fields = Ext.Object.getKeys($I18N.common.button);
    					var values = Ext.Object.getValues($I18N.common.button);
    					var data = [];
    					Ext.each(fields, function(f, index){
    						var o = {};
    						o.value = fields[index];
    						o.display = values[index];
    						data.push(o);
    					});
    					field.multistore = {fields:['display', 'value'],data:data};
    				}
    			}
    		},
    		'mygrid': {
    			select: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    				 var grid=Ext.getCmp('grid');
    					if(record&&grid.dockedItems.items[0].items.items[14]) grid.dockedItems.items[0].items.items[14].setDisabled(false);   	
    					else if(record && record.data.fd_type == 'C'&&grid.dockedItems.items[0].items.items[15]) grid.dockedItems.items[0].items.items[15].setDisabled(false);
    					else if(grid.dockedItems.items[0].items.items[15]&&grid.dockedItems.items[0].items.items[14]) {grid.dockedItems.items[0].items.items[15].setDisabled(true);
    				    grid.dockedItems.items[0].items.items[14].setDisabled(true); }
    			}
    		},
    		'mydetail': {
    			select: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'detail');
    				var grid=Ext.getCmp('detail');
    				if(record&&grid.dockedItems.items[0].items.items[14]) grid.dockedItems.items[0].items.items[14].setDisabled(false);
    				else if(record&&record.data.dg_type=='combo') grid.dockedItems.items[0].items.items[15].setDisabled(false);
    				else {
    				grid.dockedItems.items[0].items.items[15].setDisabled(true);
				    grid.dockedItems.items[0].items.items[14].setDisabled(true); }
    			}
    		},
    		'button[name=save]': {
    			click: function(btn){
    				me.save();
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    				  xtype:'erpDbfindButton',
    				});
    				btn.ownerCt.add({
    					xtype:'erpComboButton',
    				});
    			}
    		},
    		/**
    		 * 下拉框设置
    		 */
    		'erpComboButton': {
    			click: function(btn){
    				var activeTab = btn.up('tabpanel').getActiveTab();
    				var record = activeTab.down('gridpanel').selModel.lastSelected;
    				if(record) {
    					if(activeTab.id == 'maintab'){
    	    				if(record.data.fd_type == 'C') {   					
    	    					btn.comboSet(Ext.getCmp('fo_caller').value, record.data.fd_field);
    	    				}
        				} else {
        					if(record.data.dg_type == 'combo') 
        						btn.comboSet(Ext.getCmp('fo_caller').value, record.data.dg_field);
        				}
    				}
    			}
    		},
    		/**
    		 * DBFind设置
    		 */
    		'erpDbfindButton': {
    			click: function(btn){
    				var activeTab = btn.up('tabpanel').getActiveTab();
    				var record = activeTab.down('gridpanel').selModel.lastSelected;
    				if(record) {
    					if(activeTab.id == 'maintab'){
        					if(record.data.fd_dbfind != 'F') 
        						btn.dbfindSetUI(Ext.getCmp('fo_caller').value, record.data.fd_field);
        				}else {	
        					if(record.data.dg_dbbutton != '0') 
        						btn.dbfindSetGrid(Ext.getCmp('fo_caller').value, activeTab.down('gridpanel'), record.data.dg_field);
        				}
    				}
    			}
    		},
    		'button[name=delete]': {
    			afterrender:function(btn){
    				var who=getUrlParam('whoami');
    				if(who=='MarketTaskReport'||who=='TrainReport'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var type = getUrlParam('type')?getUrlParam('type'):'';
    				if(type=='crm'){
    					me.onDelete(Ext.getCmp('rt_foid').value,type);
    				}else if(type=='ProductTrain'){
    					me.onDelete(Ext.getCmp('px_foid').value,type);
    				}
    			}
    		},
    		'button[name=close]': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'textfield[name=rt_code]':{
    			beforerender:function(f){
    				var who=getUrlParam('whoami');
    				if(who!='MarketTaskReport'){
    					f.readOnly=true;
    				}
    			}
    		},
    		'textfield[name=fo_detailtable]': {
    			change: function(field){
    				var grid = Ext.getCmp('detail');
    				if(grid) {
    					var val = field.value.toUpperCase().split(' ')[0];
        				Ext.each(grid.store.items, function(){
        					var t = this.data['dg_table'];
        					if(t != null && t != ''){
        						if(val != t.toUpperCase()){
        							this.set('dg_table', val);
        						}
        					}
        				});
    				}
    			}
    		},
    		'panel[id=detailtab]': {
    			activate: function(){
    			}
    		},
    		'dbfindtrigger[name=fo_keyfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_codefield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_statusfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_statuscodefield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailkeyfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailmainkeyfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailstatuscode]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailstatus]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detaildetnofield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'button[name=preview]': {
    			click: function(){
    				this.createPreForm();
    			}
    		}
    	});
    },
    insertKeyField: function(){
    	var grid = Ext.getCmp('detail');
    	var field = Ext.getCmp('fo_detailkeyfield');
		var count = 0;
		Ext.each(grid.store.data.items, function(){
			var logic = this.data['dg_logictype'];
			var t = this.data['dg_field'];
			if(field.value.toUpperCase() == t.toUpperCase()){
				this.set('dg_logictype', 'keyField');
				logic = 'keyField';
				if(count >= 2){
					if(this.data['dg_id'] == null || this.data['dg_id'] == ''){
						grid.store.remove(this);
					}
				}
			}
			if(logic == 'keyField'){
				count++;
				if(count < 2 && field.value.toUpperCase() != t.toUpperCase()){
					this.set('dg_field', field.value);
				}
				if(count >= 2){
					if(this.data['dg_id'] == null || this.data['dg_id'] == ''){
						grid.store.remove(this);
					}
				}
			}
		});
		if(count == 0){
			grid.store.add({
				dg_sequence: grid.store.data.items[grid.store.data.length-1].data['dg_sequence'] + 1,
				dg_logictype: 'keyField',
				dg_field: field.value,
				dg_caption: 'ID',
				dg_table: Ext.getCmp('fo_detailtable').value,
				dg_caller: Ext.getCmp('fo_caller').value,
				dg_width: 0,
				dg_visible: '0',
				dg_type: 'numbercolumn',
				dg_editable: '0',
				dg_dbbutton: '0'
			});
		} else if(count > 1){
			showError("您的从表中有" + count + "个逻辑类型为[主键字段]的字段,请仔细核查!");
		}
    },
    insertMainField: function(){
    	var grid = Ext.getCmp('detail');
    	var field = Ext.getCmp('fo_detailmainkeyfield');
		var count = 0;
		Ext.each(grid.store.data.items, function(){
			var logic = this.data['dg_logictype'];
			var t = this.data['dg_field'];
			if(field.value.toUpperCase() == t.toUpperCase()){
				this.set('dg_logictype', 'mainField');
				logic = 'mainField';
				if(count >= 2){
					if(this.data['dg_id'] == null || this.data['dg_id'] == ''){
						grid.store.remove(this);
					}
				}
			}
			if(logic == 'mainField'){
				count++;
				if(count < 2 && field.value.toUpperCase() != t.toUpperCase()){
					this.set('dg_field', field.value);
				}
				if(count >= 2){
					if(this.data['dg_id'] == null || this.data['dg_id'] == ''){
						grid.store.remove(this);
					}
				}
			}
		});
		if(count == 0){
			grid.store.add({
				dg_sequence: grid.store.data.items[grid.store.data.length-1].data['dg_sequence'] + 1,
				dg_logictype: 'mainField',
				dg_field: field.value,
				dg_caption: 'MainID',
				dg_table: Ext.getCmp('fo_detailtable').value,
				dg_caller: Ext.getCmp('fo_caller').value,
				dg_width: 0,
				dg_visible: '0',
				dg_type: 'text',
				dg_editable: '0',
				dg_dbbutton: '0'
			});
		} else if(count > 1){
			showError("您的从表中有" + count + "个逻辑类型为[关联主表字段]的字段,请仔细核查!");
		}
    },
    createPreForm: function(){
    	var form = Ext.create('Ext.form.Panel', {
			region: 'center',
			layout: 'column',
			frame : true,
			autoScroll : true,
			buttonAlign : 'center'
    	});
    	var items = new Array();
    	Ext.getCmp('grid').store.each(function(item){
    		if(item.data.fd_foid!=0){
    			var data = item.data;
    			var i=new Object();
    			i.detno=data.fd_detno;
    			i.allowblank=data.fd_allowblank;
    			i.cls="form-field-allowBlank";
    			i.readonly=data.fd_readonly;
    			i.columnWidth=data.fd_columnwidth/4;
    			i.fieldLabel=data.fd_caption;
    			i.xtype='textfield';
    			if(data.fd_type=='N'){
    				i.xtype = "numberfield";
    				i.hideTrigger = true;
    			}else if(data.fd_type=='C'){
    				i.xtype = "combo";
    				i.editable = false;
    				i.queryMode = "local";
    				i.displayField = "display";
    				i.valueField = "value";
    			}else if(data.fd_type=='T'){
    				i.xtype = "textareatrigger";
    			}else if(data.fd_type=='TA'){
    				i.xtype = "textareafield";
    				i.labelAlign="top";
    			}else if(data.fd_type=='H'){
    				i.xtype = "hidden";
    				i.cls = "form-field-allowBlank-hidden";
    			}else if(data.fd_type=='D'){
    				i.xtype = "datefield";
    			}else if(data.fd_type=='Html'){
    				i.xtype = "htmleditor";
    				i.labelAlign = "top";
    			}else if(data.fd_type=='DT'){
    				i.xtype = "datetimefield";
    				i.minValue = "0:00 AM";
    				i.maxValue = "0:00 PM";
    			}
    			
    			items.push(i);
    		}
    	});
    	var win = Ext.create('Ext.window.Window',{
			title: '预 览',
    		height: 500,
    		width: 800,
    		maximizable : true,
    		closable: false,
    		buttonAlign : 'center',
    		layout : 'border',
    		bodyStyle: 'background:#f1f1f1;',
    		items:[],
    		buttons:[{
				text : '关  闭',
				flag : 'cancel',
				height : 26,
				disabled : false,
				handler : function(b) {
					var w = b.ownerCt.ownerCt;
					w.close();
				}
			}]
		});
    	items.sort(function(a,b){return a.detno>b.detno?1:-1});//从小到大排序
    	form.add(items);
		win.add(form);
		win.show();
    },
    createPreGrid: function(){
    	
    },
    createFormItem: function(record){
    	
    },	
    toSave: function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			params['param' + i] = unescape(arguments[i].toString().replace(/\\/g,"%"));
		}
		params.type = getUrlParam('type')?getUrlParam('type'):'';
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var res = new Ext.decode(response.responseText);
	   			if(res.success){
	   				saveSuccess(function(){
	   				 var url='jsps/crm/marketmgr/marketresearch/multiform.jsp?'+
	   			   'formCondition=fo_idIS'+res.fo_id+'&gridCondition=fd_foidIS'+
	   			   res.fo_id+'&whoami='+res.caller+'&cond='+res.cond+'&type='+res.type;
	   				 window.location.href=basePath+url;
	   				});
	   			} else{
	   				showError(res.exceptionInfo);
	   			}
	   		}
	   		
		});
	},
    save: function(){
    	var grid = Ext.getCmp('grid'),items = grid.store.data.items;
    	var detail = Ext.getCmp('detail');
		var me = this;
		if(! me.FormUtil.checkForm()){
			return;
		}
		var dd = grid.getChange(),de = detail.getChange();
		var who=getUrlParam('whoami');
		if(who=='MarketTaskReport'||who=='TrainReport'){//添加
			var  data=new Array();
			Ext.each(grid.store.data.items,function(item){
				var d=item.data;
				if(d['deploy'] == true){
					data.push(grid.removeKey(d, 'deploy'));
				}
			});
			var detailData=new Array();
			Ext.each(detail.store.data.items,function(item){
				var d=item.data;
				if(d['deploy'] == true){
					detailData.push(detail.removeKey(d, 'deploy'));
				}
			});
			me.toSave(Ext.getCmp('form').getValues(), Ext.encode(data), 
					Ext.encode(detailData));
		}else{//更新
			var type = getUrlParam('type')?getUrlParam('type'):'';
			me.FormUtil.update(Ext.getCmp('form').getValues(), Ext.encode(dd.added), 
					Ext.encode(dd.updated), Ext.encode(dd.deleted), Ext.encode(de.added), 
					Ext.encode(de.updated), Ext.encode(de.deleted),type);//后台是param7
		}
		
    },
    onDelete: function(id,type){
    	var me = this;
    	warnMsg($I18N.common.msg.ask_del_main, function(btn){
			if(btn == 'yes'){
				var form = Ext.getCmp('form');
				if(!me.FormUtil.contains(form.deleteUrl, '?caller=', true)){
					form.deleteUrl = form.deleteUrl + "?caller=" + caller;
				}
				Ext.Ajax.request({
					url : basePath + form.deleteUrl,
					params: {
						id: id,
						type: type
					},
					method : 'post',
					callback : function(options,success,response){
						var localJson = new Ext.decode(response.responseText);
						if(localJson.exceptionInfo){
							showError(localJson.exceptionInfo);return;
						}
						if(localJson.success){
							delSuccess(function(){
								me.FormUtil.onClose();							
							});//@i18n/i18n.js
						} else {
							delFailure();
						}
					}
				});
			}
		});
    },
});