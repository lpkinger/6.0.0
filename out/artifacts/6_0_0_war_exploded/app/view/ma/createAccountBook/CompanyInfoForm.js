// step1:企业信息表单
Ext.define('erp.view.ma.createAccountBook.CompanyInfoForm',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.companyinfo',
	id: 'createAccountBook_companyinfo',
	hideBorders: true, 
	frame: false,
	autoScroll:true,
	title: '企业信息',
	labelAlign: 'right',
	layout: {
        type: 'hbox',
        pack: 'center',
        align: 'middle'
	},
	initComponent : function(){
		var me=this;
		me.callParent(arguments);
		me.pageIndex = 0;
	},
	items:[{
		xtype: 'form',
		id:'createAccountBook_companyInfoForm',
		bodyPadding: 10,
		defaultType: 'textfield',
		cls: 'toTop',
		buttonAlign: 'center',
		
		inCloud: false,
		licenseNo: '',
		hasCreate: false,
		companyName: '',
		error: false,
		
		defaults: {
			labelAlign: 'right',
			allowBlank: false,
			msgTarget: 'side',
			width: 500,
			padding: '5 0'
		},
		items: [{
            fieldLabel: '企业名称',
            id: 'createAccountBook_companyInfo_name',
            name: 'companyName',
            emptyText: '填写营业执照上的企业名称',
            validateOnChange: false,
            validateOnBlur: true,
            validator: function(value) {
            	var form = this.up('form');
            	if(form.error) {
            		return '检验异常！请检查网络连接。';
            	}
				if(form.hasCreate) {
					return '当前企业已经存在关联的企业管理系统！';
				}else {
					return true;
				}
            }
        }, {
            fieldLabel: '企业简称',
            name: 'companyShortName',
            minLength: 2,
            maxLength: 40,
            minLengthText: '长度不能小于2个字符！',
            maxLengthText: '长度不能大于40个字符！',
            emptyText: '请填写正确的企业名称，2-40个字符'
        }, {
        	xtype: 'fieldset',
        	layout: 'hbox',
        	cls: 'license-show',
	        items:[{
	        	xtype: 'hidden',
	        	name: 'licensePath',
	        	fieldLabel: '营业执照文件路径'
	        },{
	        	xtype: 'textfield',
	        	fieldLabel: '营业执照',
	        	readOnly: true,
	        	flex: 9,
	        	name: 'licenseName',
	        	allowBlank: false,
	        	emptyText: '点击上传营业执照扫描件,≤5M的图片或PDF'
	        }, {
	        	xtype: 'mfilefield',
	            name: 'licenseFile',
	        	title: '营业执照',
	        	flex: 1,
	        	multi: false
	        }]
        }, {
            fieldLabel: '营业执照号',
            id: 'companyInfo_licenseNo',
            name: 'licenseNo',
            emptyText: '填写营业执照上的注册号',
            validateOnChange: false,
            validator: function(value) {
            	var form = this.up('form');
				
				// 获得表单企业名称项
				var companyName = Ext.getCmp('createAccountBook_companyInfo_name').getValue();
				if(form.error) {
            		return '检验异常！请检查网络连接。';
            	}
            	if(form.inCloud) {
            		if(form.licenseNo == value) {
            			return true;
            		}else {
            			return '营业执照号与注册优软云企业填写的营业执照号不一致，请联系优软云服务团队800-830-1818。';
            		}
            	}else {
            		if(form.companyName) {
            			return '营业执照号已存在对应企业！'
            		}else {
            			return true;
            		}
            	}
            }
        }, {
            fieldLabel: '企业地址',
            name: 'companyAddr',
            emptyText: '总部所在地的详细地址'
        }, {
            fieldLabel: '管理员',
            name: 'managerName'
        }, {
            fieldLabel: '管理员手机',
            vtype: 'phone',
            name: 'managerPhone'
        }, {
            fieldLabel: '管理员邮箱',
            vtype: 'email',
            vtypeText: '这不是一个正确的邮箱账号！',
            name: 'managerEmail'
        }],
	    buttons: [{
	    	id: 'companyInfoNextBtn',
	        text: '下一步',
	        formBind: true,
			cls: 'form-button'
	    }]
	}]
});

Ext.apply(Ext.form.field.VTypes, {
    phone:  function(v) {
        return /^((\d{3,4}-)*\d{7,8}(-\d{3,4})*|((13|14|15|17|18)\d{9}))$/.test(v);
    },
    phoneText: '这不是一个正确的手机号码！',
    phoneMask: /[\d]/i,
});
