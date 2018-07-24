
Ext.namespace("Ext.ux");

Ext.ux.LoginWindow = Ext.extend(Ext.Window, {
    title: '登陆',
    width: 265,
    height: 140,
    collapsible: true,
    closable: false,
    modal: true,
    defaults: {
        border: false
    },
    buttonAlign: 'center',

    initComponent : function(){
        this.keys = {
            key: Ext.EventObject.ENTER,
            fn: this.login,
            scope: this
        };
        Ext.ux.LoginWindow.superclass.initComponent.call(this);
        this.formPanel = this.createFormPanel();
        this.add(this.formPanel);
        this.addButton('登陆', this.login, this);
        this.addButton('重填', function() {
            this.formPanel.getForm().reset();
        }, this);

        var form = this.formPanel.getForm();
        var fn = function() {
            //form.suspendEvents();
            form.findField('j_username').focus();
            //form.resumeEvents();
        }

        this.on('show', function() {
            setTimeout(fn, 200);
        }, this);
    },

    createFormPanel: function() {
        return new Ext.form.FormPanel({
            bodyStyle: 'padding-top:6px',
            defaultType: 'textfield',
            labelAlign: 'right',
            labelWidth: 55,
            labelPad: 0,
            frame: true,
            defaults: {
                allowBlank: false,
                width: 158,
                selectOnFocus: true
            },
            items: [{
                cls: 'login-username',
                name: 'j_username',
                fieldLabel: '用户名',
                blankText: '用户名不能为空'
            },{
                cls: 'login-password',
                name: 'j_password',
                fieldLabel: '密  码',
                blankText: '密码不能为空',
                inputType: 'password'
            }]
        });
    },

    login: function() {
        if (this.formPanel.form.isValid()) {
            this.formPanel.form.submit({
                waitTitle: "请稍候",
                waitMsg : '正在登录......',
                url: this.url,
                success: function(form, action) {
                    this.hide();
                    if (this.callback) {
                        this.callback.call(this, action.result);
                    }
                },
                failure: function(form, action) {
                    if (action.failureType == Ext.form.Action.SERVER_INVALID) {
                        Ext.Msg.alert('错误', action.result.errors.msg);
                    }
                    form.findField("j_password").setRawValue("");
                    form.findField("j_username").focus(true);
                },
                scope:this
            });
        }
    }
});

