Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.assetsDepreciation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.fix.assetsDepreciation','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.Post','core.button.ResPost',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField'
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
       				if(me.checkAmount()){
	        			if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	        				me.BaseUtil.getRandomNumber();//自动添加编号
	        			}
        				this.FormUtil.beforeSave(this);
       				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('de_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
       				if(me.checkAmount()){
    					this.FormUtil.onUpdate(this);
       				}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('add'+caller , '新增单据', "jsps/fa/fix/assetsDepreciation.jsp?whoami=" + caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('de_statuscode');
    				if(status && status.value != 'UNPOST'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
       				if(me.checkAmount()){
    					me.FormUtil.onSubmit(Ext.getCmp('de_id').value);
       				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('de_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('de_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var reportName = 'Assets_Depreciation',
    					id = Ext.getCmp('de_id').getValue(),
    					condition = '{AssetsDepreciation.de_id}=' + id;
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('de_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
       				if(me.checkAmount()){
    					me.FormUtil.onPost(Ext.getCmp('de_id').value);
       				}
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('de_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('de_id').value);
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
	checkAmount: function() {
		var me = this, grid = Ext.getCmp('grid'), bool = true;
		var datas = grid.store.data.items;
		var str = '';
		for(var i=0;i<datas.length-1;i++){
			for(var j=i+1;j<datas.length;j++){
				if(datas[i].data['dd_accode']!=''&&datas[i].data['dd_accode']==datas[j].data['dd_accode']){
	                str += '、'+datas[j].data['dd_detno']
				}
				if(str!=''){
					showError('明细表第'+datas[i].data['dd_detno']+str+'行的卡片：'+datas[i].data['dd_accode']+'重复，不予许操作！');
					return false;
				}
			}
		}
		var err = '';
		grid.store.each(function(item) {
            if (!Ext.isEmpty(item.data['dd_accode'])) {
            	if('AssetsDepreciation' == caller){
            		if (me.BaseUtil.numberFormat(item.data['dd_amount'], 2) > me.BaseUtil.numberFormat(item.data['ac_netvalue'] - item.data['ac_cvalue'], 2)) {
	                    bool = false;
	                    showError('明细表第' + item.data['dd_detno'] + '行的折旧金额大于净值-净残值');
	                    return;
                	}
				}
                if('AssetsDepreciation!Reduce' == caller){
                	if(item.data['dd_amount']==0||item.data['dd_amount']==''||item.data['dd_amount']==null){
                		err += '、'+ item.data['dd_detno'];
                	}
					if (me.BaseUtil.numberFormat(item.data['dd_amount'], 2) > me.BaseUtil.numberFormat(item.data['ac_oldvalue'], 2)) {
	                    bool = false;
	                    showError('明细表第' + item.data['dd_detno'] + '行的减少金额大于原值');
	                    return;
                	}
				}
            }
		});
		if(err!=''){
	        showError('减少金额不能为0！行' + err.substring(1));
         	return false;
		}
		return bool;
	}
});