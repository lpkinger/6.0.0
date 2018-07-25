Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.TaskTemplates', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'crm.marketmgr.marketresearch.TaskTemplates','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					if(me.check()){
						return;
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('tt_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					if(me.check()){
						return;
					}
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addTaskTemplates', '新增调研计划', 'jsps/crm/marketmgr/marketresearch/taskTemplates.jsp');
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
	getGriddata: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var jsonGridData = new Array();
		var s = grid.getStore().data.items;//获取store里面的数据
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			jsonGridData.push(Ext.JSON.encode(data));
		}
		return jsonGridData;
	},
	check:function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var flag=false;
		Ext.each(items,function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['ttd_reporttemplatecode']==null||item.data['ttd_reporttemplatecode']==''){
					showError('第'+item.data['ttd_detno']+'行的任务报告类型没选或无效！');flag=true;return;
				}
				if(item.data['ttd_standardtime']==null||item.data['ttd_standardtime']<0){
					showError('第'+item.data['ttd_detno']+'行的标准工时无效！');flag=true;return;
				}
			}
		});
		return flag;
	}
});