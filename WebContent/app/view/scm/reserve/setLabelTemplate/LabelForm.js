Ext.define('erp.view.scm.reserve.setLabelTemplate.LabelForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpLabelFormPanel',
	id: 'labelForm', 
	region: 'north',
	frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	cls: 'u-form-default',
	fieldDefaults : {
		fieldStyle : "background:#FFFAFA;color:#515151;",
		focusCls: 'x-form-field-cir-focus',
		labelAlign : "right",
		msgTarget: 'side',
		blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),	
	keyField: '',
	codeField: '',
	statusField: '',
	params: null,
	caller: null,
	formCondition:null,
	Contextvalue:null,
	LastValue:null,
	enableTools: true,
	enableKeyEvents: true,
	_noc: 0,
	initComponent : function(){ 
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		var param = {caller: 'LabelTemplateBarcode',condition:'', _noc: (getUrlParam('_noc') || this._noc)};		
		this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);//从后台拿到formpanel的items
		this.callParent(arguments);		
		//给页面加上ctrl+alt+s键盘事件,自动跳转form配置界面
		if(this.enableKeyEvents) {
			this.addKeyBoardEvents();
		}		
	},
	/**
	 * 监听一些事件
	 * <br>
	 * Ctrl+Alt+S	单据配置维护
	 * Ctrl+Alt+P	参数、逻辑配置维护
	 */
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey) {
				if(e.keyCode == Ext.EventObject.S) {
					var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
						forms = Ext.ComponentQuery.query('form'), 
						grids = Ext.ComponentQuery.query('gridpanel'),
						formSet = [], gridSet = [];
					if(forms.length > 0) {
						Ext.Array.each(forms, function(f){
							f.fo_id && (formSet.push(f.fo_id));
						});
					}
					if(grids.length > 0) {
						Ext.Array.each(grids, function(g){
							if(g.xtype.indexOf('erpGridPanel') > -1)
								gridSet.push(window.caller);
							else if(g.caller)
								gridSet.push(g.caller);
						});
					}
					if(formSet.length > 0 || gridSet.length > 0) {
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
					}
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				}
			}
		});
	},
	
	getDataByField : function(field) {
		var form = this, f = form.child('#' + field);
		return f ? "'" + f.getValue() + "'" : '';
	},
	getYearmonthByField : function(field) {
		var form = this;
		var f = form.child('#' + field),
		v = f ? (Ext.isDate(f.value) ? f.value : Ext.Date.parse(f.value, 'Y-m-d')) : new Date();
		return Ext.Date.format(v, 'Ym');
	}
});