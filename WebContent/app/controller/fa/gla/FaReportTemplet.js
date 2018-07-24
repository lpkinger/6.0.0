Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.FaReportTemplet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gla.FaReportTemplet','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			afterrender: function(field){
    				setTimeout(function(){
	    				var name = Ext.getCmp('ft_name').value, grid = Ext.getCmp('grid');
	    				if(name != '资产负债表'){
	    					grid.down('gridcolumn[dataIndex=fd_rightname]').hide();
	    					grid.down('gridcolumn[dataIndex=fd_rightstep]').hide();
	    					grid.down('gridcolumn[dataIndex=fd_rightstandard]').hide();
	    				} else {
	    					grid.down('gridcolumn[dataIndex=fd_rightname]').show();
	    					grid.down('gridcolumn[dataIndex=fd_rightstep]').show();
	    					grid.down('gridcolumn[dataIndex=fd_rightstandard]').show();
	    				}
    				},200);
    			},
    			itemclick: this.onGridItemClick
    		},
    		'combo[name=ft_name]': {
    			beforerender: function(field){
					if(Ext.getCmp('ft_code')&&Ext.getCmp('ft_code').value){
						field.readOnly=true;
					}
				},
    			delay: 200,
    			change: function(m){
    				var grid = Ext.getCmp('grid');
    				if(m.value != '资产负债表'){
    					grid.down('gridcolumn[dataIndex=fd_rightname]').hide();
    					grid.down('gridcolumn[dataIndex=fd_rightstep]').hide();
    					grid.down('gridcolumn[dataIndex=fd_rightstandard]').hide();
    				} else {
    					grid.down('gridcolumn[dataIndex=fd_rightname]').show();
    					grid.down('gridcolumn[dataIndex=fd_rightstep]').show();
    					grid.down('gridcolumn[dataIndex=fd_rightstandard]').show();
    				}
				}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					Ext.getCmp(form.codeField).setValue(me.BaseUtil.getRandomNumber());//自动添加编号
    				}
    				me.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ab_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addFaReportTemp', '新增集团合并报表格式', 'jsps/fa/gla/faReportTemp.jsp?whoami='+caller);
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
	}
});