Ext.define('erp.view.oa.officialDocument.instruction.InstructionDetailForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpInstructionDetailFormPanel',
	id: 'form', 
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    region: 'center',
    frame : true,
    fieldDefaults: {
        labelWidth: 80,
        cls: 'form-field-allowBlank'
    },
    layout: {
        type: 'vbox',//hbox水平盒布局 
        align: 'stretch'  // Child items are stretched to full width子面板高度充满父容器 
    },
    items: [{
        xtype: 'displayfield',
        fieldLabel: 'ID',
        id:'in_id',
        name: 'in_id'
    },{
        xtype: 'displayfield',
        fieldLabel: '请示类型',
        id:'in_type',
        name: 'in_type'
    }, {
        xtype: 'displayfield',
        fieldLabel: '请示日期',
        id:'in_date',
        name: 'in_date'
    }, {
        xtype: 'displayfield',
        fieldLabel: '请示标题',
        id:'in_title',
        name: 'in_title'
    }, {
        xtype: 'displayfield',
        fieldLabel: '请示部门',
        name: 'in_dept',
        id: 'in_dept'
    }, {
        xtype: 'displayfield',
        fieldLabel: '秘密等级',
        name: 'in_secretlevel',
        id: 'in_secretlevel'
    }, {
        xtype: 'displayfield',
        fieldLabel: '紧急程度',
        name: 'in_emergencydegree',
        id: 'in_emergencydegree'
    }, {
    	 xtype: 'displayfield',
         fieldLabel: '附件',
         name: 'in_attach',
         id: 'in_attach'
    },{
        xtype: 'htmleditor',
        hideLabel: true,
        autoScroll: true,
        readOnly: true,
        height: height*0.8,
        name: 'in_context',
        id: 'in_context',
        anchor: '100%'//编辑框******
    }],
    buttonAlign: 'center',
    buttons: [{
    	id: 'close',
    	text: '关闭',
    	iconCls: 'group-close',
    	cls: 'x-btn-gray'
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		this.getInstructionDetail(getUrlParam('id'));
	},
	getInstructionDetail: function(id){
		var me = this;
//		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "oa/officialDocument/getInstructionDetail.action",
        	params: {
        		id: id
        	},
        	method : 'post',
        	callback : function(options, success, response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		console.log(response);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.instruction){
        			return;
        		} else {
//        			var day = new Date(Ext.util.Format.date(new Date(res.rod.rod_date))); //将日期值格式化 
//        			var today = new Array("星期天","星期一","星期二","星期三","星期四","星期五","星期六"); 
        			Ext.getCmp('in_date').setValue(Ext.util.Format.date(new Date(res.instruction.in_date)));// + "(" + today[day.getDay()] + ")");
        			Ext.getCmp('in_type').setValue(res.instruction.in_type);
//        			Ext.getCmp('in_number').hide().setValue(res.instruction.in_number);
        			Ext.getCmp('in_id').hide().setValue(res.instruction.in_id);
        			Ext.getCmp('in_title').setValue(res.instruction.in_title);
//        			Ext.getCmp('rod_subject').setValue(res.rod.rod_subject);
        			Ext.getCmp('in_dept').setValue(res.instruction.in_dept);
        			Ext.getCmp('in_secretlevel').setValue(res.instruction.in_secretLevel);
        			Ext.getCmp('in_emergencydegree').setValue(res.instruction.in_emergencyDegree);
        			Ext.getCmp('in_context').setValue(res.instruction.in_context);
//        			Ext.getCmp('rod_registrant').setValue(res.rod_registrant);
        			if(res.in_attach != ""){
        				var attach = res.in_attach.split(';');
        				var text = "";
        				Ext.each(attach, function(a, index){
        					var path = a.toString();
        					if(me.BaseUtil.contains(a, '\\', true)){
        						text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('\\') + 1) + "</a>";
        					} else {
        						text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('/') + 1) + "</a>";
        					}
        				});
        				Ext.getCmp('in_attach').setValue(text);
        			} else {
        				Ext.getCmp('in_attach').hide();
        			}
        		}
        	}
        });
	}
});