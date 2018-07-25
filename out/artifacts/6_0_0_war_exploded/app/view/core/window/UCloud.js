Ext.define('erp.view.core.window.UCloud', {
	extend: 'Ext.window.Window',
	alias: 'widget.ucloud',
	id:'ucloud',
	width: 540,
	height: 500,
	frame: true,
	resizable:false,//指定为“真”允许用户调整每个边缘和窗口的角落，为假时禁用调整。
	modal: true,
	bodyStyle:"background-image: url('" + basePath + "resource/images/ucloud.png');background-size:580px 560px;",
	layout: 'absolute',
	lableWidth:50,
	draggable:false,
	closable : false,
    buttonAlign:'center',
    items:[{
    	xtype:'button',
        text:'立即初始化',
        scale:'medium',
        width:100,
        x:220,
        y:380,
        handler:function(){
			var url='system/init.action';
    		window.open(basePath+url,'_blank');
			var win = Ext.getCmp('ucloud');
			win.close();
        }
    },{
    	xtype:'button',	
        text:' 下次再说',
        scale:'medium',
        width:100,
        x:220,
        y:420,
        handler:function(){
        	var win = Ext.getCmp('ucloud');
        	win.close();
        }       	
    },{
    	xtype:'button',
    	icon:basePath+'resource/images/uClose.png',
    	cls:'UClose',
    	x:530,
        y:10,
    	handler:function(){
    		var win = Ext.getCmp('ucloud');
        	win.close();
    	}
    },{
    	xtype:'checkbox',
    	id:'nextsaid',
    	boxLabel:'下次登入不再提醒',
    	boxLabelAlign:'after',
    	x:20,
    	y:475
    }],
    listeners:{
    	beforeclose:function(win){
    		var checked = win.down('checkbox').checked;
			Ext.Ajax.request({
		    	url : basePath + 'ma/sysinit/insertReadStatus.action',
				params: {
					status: checked,
					man:em_id,
					sourcekind:'ucloud'
				},
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.exceptionInfo){
						showError(r.exceptionInfo);return;
					}else if(r.success){
						
					}
				}
			})
    	}
    },
	initComponent: function() {
		this.callParent(arguments);
	}
});