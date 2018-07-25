Ext.define('erp.view.ma.SysCheckWinGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpSysCheckWinGrid',
	requires: ['erp.view.core.toolbar.Toolbar'],
	id: 'wingrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    })],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    necessaryField: '',//必填字段
    detno: '',//编号字段
    keyField: '',//主键字段
	mainField: '',//对应主表主键的字段
	dbfinds: [],
	caller: null,
	condition: null,
	initComponent : function(){
		var condition = this.condition;
		if(!condition){
			var urlCondition = this.BaseUtil.getUrlParam('gridCondition');
			urlCondition = urlCondition == null || urlCondition == "null" ? "" : urlCondition;
			gridCondition = (gridCondition == null || gridCondition == "null") ? "" : gridCondition;
			gridCondition = gridCondition + urlCondition;
	    	gridCondition = gridCondition.replace(/IS/g, "=");
			/*if(gridCondition.search(/!/) != -1){
				gridCondition = gridCondition.substring(0, gridCondition.length - 4);
			}*/
			condition = gridCondition;
		}
    	var gridParam = {caller: this.caller || caller, condition: condition};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		this.callParent(arguments); 
	},
	getEffectiveData: function(){
		var me = this;
		var effective = new Array();
		var s = this.store.data.items;
		for(var i=0;i<s.length;i++){
			var data = s[i].data;
			if(data[me.keyField] != null && data[me.keyField] != ""){
				effective.push(data);
			}
		}
		return effective;
	},
	setReadOnly: function(bool){
		this.readOnly = bool;
	},
	reconfigure: function(store, columns){
    	var d = this.headerCt;
    	if (this.columns.length <= 1 && columns) {
			d.suspendLayout = true;
			d.removeAll();
			d.add(columns);
		}
		if (store) {
			try{
				this.bindStore(store);
			} catch (e){
				
			}
		} else {
			this.getView().refresh();
		}
		if (columns) {
			d.suspendLayout = false;
			this.forceComponentLayout();
		}
		this.fireEvent("reconfigure", this);
    },
	listeners: {
		afterrender: function(grid){
			var me = this;
			if(Ext.isIE && !Ext.isIE11){
				document.body.attachEvent('onkeydown', function(){
					if(window.event.ctrlKey && window.event.keyCode == 67){//Ctrl + C
						var e = window.event;
						if(e.srcElement) {
							window.clipboardData.setData('text', e.srcElement.innerHTML);
						}
					}
				});
			} else {
				document.body.addEventListener("mouseover", function(e){
					if(Ext.isFF5){
						e = e || window.event;
					}
					window.mouseoverData = e.target.value;
		    	});
				document.body.addEventListener("keydown", function(e){
					if(Ext.isFF5){
						e = e || window.event;
					}
					if(e.ctrlKey && e.keyCode == 67){
						me.copyToClipboard(window.mouseoverData);
					}
					if(e.ctrlKey && e.keyCode == 67){
						me.copyToClipboard(window.mouseoverData);
					}
		    	});
			}
		}
	},
	copyToClipboard: function(txt) {
		if(window.clipboardData) { 
			window.clipboardData.clearData(); 
			window.clipboardData.setData('text', txt); 
		} else if (navigator.userAgent.indexOf('Opera') != -1) { 
			window.location = txt; 
		} else if (window.netscape) { 
			try { 
				netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect'); 
			} catch (e) { 
				alert("您的firefox安全限制限制您进行剪贴板操作，请打开'about:config'将signed.applets.codebase_principal_support'设置为true'之后重试"); 
				return false; 
			}
		}
	}
});