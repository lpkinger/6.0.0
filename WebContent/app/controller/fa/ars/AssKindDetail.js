Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.AssKindDetail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.AssKindDetail','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ak_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addAssKindDetail', '新增核算项值', 'jsps/fa/ars/assKindDetail.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
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
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var bool = true, codes = {}, t = new Array();
		//判断核算编码是否重复
		Ext.each(items, function(item){
			var v = item.get('akd_asscode');
			if(!codes[v]) {
				codes[v] = [item.get('akd_detno')];
			} else {
				codes[v] = codes[v].push(item.get('akd_detno'));
				t.push(v);
			}
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['akd_asscode'] == null){
					bool = false;
					showError('明细表第' + item.data['akd_detno'] + '行，核算编码已经存在!');return;
				}
			}
		});
		if(t.length > 0) {
			
			return;
		}
		if(bool)
			this.FormUtil.beforeSave(this);
	}
});