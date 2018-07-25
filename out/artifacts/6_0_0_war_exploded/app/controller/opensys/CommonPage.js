Ext.QuickTips.init();
Ext.define('erp.controller.opensys.CommonPage', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views:['opensys.commonpage.ViewPort','core.form.Panel2',
			'core.trigger.DbfindTrigger','core.trigger.AddDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 
			'core.form.MultiField','core.form.YnField','core.trigger.TextAreaTrigger','core.form.FileField',
           'core.button.Save','core.button.Close','core.button.Add','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
           'core.button.Audit','core.button.ResAudit','core.button.OpensysConfirm','core.form.CheckBoxGroup','core.button.PrintPDF'
	       ],
	init:function(){
    	var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');
		this.control({
			'dbfindtrigger':{
				afterrender:function(f){
					Ext.apply(f, {
						 extend: 'Ext.form.field.Trigger',
    				     triggerCls: 'x-form-search-trigger',
    				     selecteddata:new Array(),
    				     initComponent: function() {
    					   this.addEvents({
    							aftertrigger: true,
    							beforetrigger: true
    					   });
    					   this.callParent(arguments);  
    				   },
    				    onTriggerClick:this.onTriggerClick
					});
				}
			},
			'htmleditor': {
    			afterrender: function(f){
    				if(!f.defaultValue){
    					f.defaultValue='';
    				}
    			}
    		},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			reconfigure: function(grid){
    				var form = Ext.getCmp('form');
        			if(form)
        				me.resize(form, grid);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(!Ext.isEmpty(form.codeField) && Ext.getCmp(form.codeField) && ( 
    						Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				var title = btn.ownerCt.ownerCt.title || ' ';
    				var url = window.location.href;
    				url = url.replace(basePath, '');
    				url = url.substring(0, url.lastIndexOf('formCondition')-1);
    				me.FormUtil.onAdd('add' + caller, title, url);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE' && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onBanned(crid);
				}
			},
			'erpResBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'DISABLE'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResBanned(crid);
				}
			},
			'erpEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE' && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onEnd(crid);
				}
			},
			'erpResEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResEnd(crid);
				}
			},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpConfirmButton': {
    			click: function(btn){
    				me.FormUtil.onConfirm(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},onTriggerClick: function() {
		var trigger = this,
		bool = true; // 放大镜所在	
		bool = trigger.fireEvent('beforetrigger', trigger);
		dbCaller=this.dbCaller|| (typeof caller === 'undefined' ? '' : caller);
		if (bool == false) {
			return;
		}
		this.setFieldStyle('background:#C6E2FF;');
		var key = this.triggerName||this.name,
		// name属性
		dbfind = '',
		// 需要dbfind的表和字段
		dbBaseCondition = '',
		dbCondition = '',
		dbGridCondition = '',
		findConfig = this.findConfig,
		dbKey = this.dbKey,
		mappingKey = this.mappingKey,
		gridKey = this.gridKey,
		mappinggirdKey = this.mappinggirdKey;
		window.onTriggerClick = this.id;
		// 存在查询条件的字段
		if (findConfig) {
			dbCondition = (typeof findConfig == 'function' ? findConfig.call(null) : findConfig);
		}
		if (dbKey) {
			var dbKeyValue = Ext.getCmp(dbKey).value;
			if (dbKeyValue) {
				dbCondition = mappingKey + " IS '" + dbKeyValue + "'";
			} else {
				showError(this.dbMessage);
				return
			}
		}
		if (gridKey) {
			var gridKeys = gridKey.split('|');
			var mappinggirdKeys = mappinggirdKey.split('|');
			var gridErrorMessages = this.gridErrorMessage.split('|');
			for (var i = 0; i < gridKeys.length; i++) {
				var gridkeyvalue = Ext.getCmp(gridKeys[i]).value;
				if (i == 0) {
					if (gridkeyvalue) {
						dbGridCondition = mappinggirdKeys[i] + " IS '" + gridkeyvalue + "' ";
					} else {
						showError(gridErrorMessages[i]);
						return
					}
				} else {
					if (gridkeyvalue) {
						dbGridCondition = dbGridCondition + " AND " + mappinggirdKeys[i] + " IS '" + gridkeyvalue + "' ";
					} else {
						showError(gridErrorMessages[i]);
						return
					}
				}
			}
		}
		if (this.dbBaseCondition) {
			dbBaseCondition = this.dbBaseCondition;
		}
		if (!trigger.ownerCt || trigger.column) { // 如果是grid的dbfind
			var grid = Ext.ComponentQuery.query('gridpanel');
			if(!trigger.dbfind){
				if(trigger.column.dataIndex == key){//
					dbfind = trigger.column.dbfind;
				}else{
					Ext.Array.each(grid,
							function(g, index) {
						Ext.Array.each(g.columns,
								function(column) {
							if (column.dataIndex == key ) {
								dbfind = column.dbfind;
								trigger.owner = g;
							}
						});
					});
				}
			}else dbfind=trigger.dbfind;

		}
		var keyValue = this.value, ob = this.dbOrderby || ''; // 当前值
		keyValue = keyValue == null ? '': keyValue;
		var width = Ext.isIE ? screen.width * 0.7 * 0.9 : '80%',
				height = Ext.isIE ? screen.height * 0.75 : '95%';
		//针对有些特殊窗口显示较小
		width =this.winWidth ? this.winWidth:width;
		height=this.winHeight ? this.winHeight:height;
		var dbwin = new Ext.window.Window({
			id: 'dbwin',
			title: '查找',
			height: height,
			width: width,
			maximizable: true,
			buttonAlign: 'center',
			layout: 'anchor',
			items: [{
				tag: 'iframe',
				frame: true,
				anchor: '100% 100%',
				layout: 'fit',
				html: '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/opensys/dbfind.jsp?key=' + key + "&dbfind=" + dbfind + "&dbGridCondition=" + encodeURIComponent(dbGridCondition) + "&dbCondition=" + encodeURIComponent(dbCondition) + "&dbBaseCondition=" + encodeURIComponent(dbBaseCondition) + "&keyValue=" + encodeURIComponent(keyValue) + "&trigger=" + trigger.id + "&caller=" + dbCaller + "&ob=" + ob + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}],
			buttons: [{
				text: '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler: function() {
					Ext.getCmp('dbwin').close();
				}
			},
			{
				text: '重置条件',
				id: 'reset',
				cls: 'x-btn-gray',
				hidden: true,
				handler: function() {
					var grid = Ext.getCmp('dbwin').el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
					grid.resetCondition();
					grid.getCount();
				}
			}]
		});
		dbwin.show();
		trigger.lastTriggerId = null;
	}
});