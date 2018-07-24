Ext.define('erp.view.common.sysinit.MoudleConPortal', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.moudleconportlet',
	layout: 'column',
	margin: '4 0 0 4',
	border:false,
	autoScroll:true,
	defaults: {
		xtype: 'checkbox',
		readOnly:true,
		columnWidth: .33
	},
    initComponent: function(){
    	this.getItems(0);
        this.callParent(arguments);
    },
    getItems:function(pid){
		var me = this;
		Ext.Ajax.request({// 拿到tree数据
        	url : basePath + 'system/initTree.action',
        	params: {
        		pid:pid,
        		_noc:1
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			Ext.Array.each(res.tree,function(item){
        				me.add({boxLabel:item.in_desc});
        			});
        			me.add({
        				padding:'10 0 0 17',
        				xtype:'tbtext',
        				columnWidth:1,
        				style:{
        					padding:'10 0 0 17',
        					color:'gray ! important'
        				},
        				text:'√ 为已启用模块'
        			})
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	
    }
});
