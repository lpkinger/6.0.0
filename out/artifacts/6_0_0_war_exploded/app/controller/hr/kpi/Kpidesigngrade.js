Ext.QuickTips.init();
Ext.define('erp.controller.hr.kpi.Kpidesigngrade', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.kpi.Kpidesigngrade','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.form.ScopeField',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpSaveButton': {
				click: function(btn){
					//保存之前的一些前台的逻辑判定
					var from=Ext.getCmp('kg_score_from').value;
					var score_to=Ext.getCmp('kg_score_to').value;
					if((from==null)||(score_to==null)||(score_to=='')){alert(1);
						showError("分数范围设置有误");
					}else if((from-0)<0||(score_to-0)<0||(from-0)>100||(score_to-0)>100||(from-0)>=(score_to-0)){
						alert(2);	showError("分数范围设置有误");
					}else{
						this.FormUtil.beforeSave(this);
					}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('kg_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var score_from=Ext.getCmp('kg_score_from').value;
					var score_to=Ext.getCmp('kg_score_to').value;
					if(score_from==null||score_to==null||score_to==''){
						showError("分数范围设置有误");
					}else if((score_from-0)<0||(score_to-0)<0||(score_from-0)>100||(score_to-0)>100||(score_from-0)>=(score_to-0)){
							showError("分数范围设置有误");
					}else{
						this.FormUtil.onUpdate(this);
					}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addKpiType', '新增考核等级', 'jsps/hr/kpi/kpiDesigngrade.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
			
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});