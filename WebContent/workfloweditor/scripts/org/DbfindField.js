Gef.ns('Gef.org');
Gef.org.DbfindField = Ext.extend(Ext.form.TriggerField, {
	triggerClass : "x-form-search-trigger",
	initComponent: function() {
		this.readOnly = false;
		Gef.org.OrgField.superclass.initComponent.call(this);
		this.addEvents('select');
	},
	onTriggerClick: function() {
		this.showWindow();
	},
	showWindow: function(store) {

		var trigger = this, bool = true;// 放大镜所在	
		bool = trigger.fireEvent('beforetrigger', trigger);
		if(bool == false) {
			return;
		}		  
		var key = this.name,// name属性
		dbfind = '',// 需要dbfind的表和字段
		dbBaseCondition='',
		dbCondition='',
		dbGridCondition='',
		findConfig=this.findConfig,
		dbKey=this.dbKey,
		mappingKey=this.mappingKey,
		gridKey=this.gridKey,
		mappinggirdKey=this.mappinggirdKey;
		window.onTriggerClick = this.id;
		// 存在查询条件的字段
		if(findConfig){
			dbCondition = findConfig;
		}
		if(dbKey){
			var dbKeyValue = Ext.getCmp(dbKey).value;
			if(dbKeyValue){
				dbCondition = mappingKey + " IS '" + dbKeyValue + "'";
			} else {
				showError(this.dbMessage);
				return
			}
		}
		if(gridKey){

			var gridKeys = gridKey.split('|');
			var mappinggirdKeys = mappinggirdKey.split('|');
			var gridErrorMessages = this.gridErrorMessage.split('|');

			for(var i=0;i<gridKeys.length;i++){
				var gridkeyvalue = Ext.getCmp(gridKeys[i]).value;

				if(i==0){
					if(gridkeyvalue){
						dbGridCondition = mappinggirdKeys[i] + " IS '"+gridkeyvalue+"' ";
					}else{
						showError(gridErrorMessages[i]);
						return
					}
				}else{
					if(gridkeyvalue){
						dbGridCondition =dbGridCondition+" AND "+ mappinggirdKeys[i] + " IS '"+gridkeyvalue+"' ";
					}else{
						showError(gridErrorMessages[i]);
						return
					}

				}

			}

		}
		if(this.dbBaseCondition){
			dbBaseCondition = this.dbBaseCondition;
		}
		if(!trigger.ownerCt){// 如果是grid的dbfind
			var grid = Ext.getCmp('grid');
			dbfind = trigger.dbfind;
			trigger.record=grid.selModel.selection.record;
			trigger.owner = grid;
		}
		var keyValue = this.value;// 当前值
		keyValue = keyValue == null ? '' : keyValue;
		var caller="Jprocess";
		var width = Ext.isIE ? screen.width*0.7*0.9 : '50%';
		var dbwin = new Ext.Window({
			id : 'dbwin',
			title: '查找',
			height: 500,
			width:width,
			x: 60,  
			frame:true,
			buttonAlign : 'center',
			layout : 'anchor',
			modal: true,
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_dbfind" src="'+basePath+'jsps/common/dbfind.jsp?key='+key+"&dbfind="+dbfind+"&dbGridCondition="+dbGridCondition+"&dbCondition="+dbCondition+"&dbBaseCondition="+dbBaseCondition+"&keyValue="+encodeURIComponent(keyValue)+"&trigger="+trigger.id+ "&caller=" + caller + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}],
			buttons : [{
				text : '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler : function(){
					Ext.getCmp('dbwin').close();
				}
			},{
				text: '重置条件',
				id: 'reset',
				cls: 'x-btn-gray',
				hidden: true,
				handler: function(){
					var grid = Ext.getCmp('dbwin').el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
					grid.resetCondition();
					grid.getCount();
				}
			}]
		});
		dbwin.show();
		trigger.lastTriggerId = null;
		Gef.activeEditor.disable();
	},

	hideWindow: function() {
		this.getWindow().hide();
		Gef.activeEditor.enable();
	},

	getWindow: function(store) {
		if (!this.orgWindow) {
			this.orgWindow = this.createWindow(store);
		}
		return this.orgWindow;
	}
});

Ext.reg('dbfindfield', Gef.org.DbfindField);

