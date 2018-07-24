/**
 * 会议室资源查看 
 */
Ext.define('erp.view.core.trigger.SchedulerTrigger', {
	extend: 'Ext.form.field.Trigger',
	alias: 'widget.SchedulerTrigger',
	triggerCls: 'x-form-color-trigger',
	onTriggerClick: function() {
		 var trigger = this,
	        bool = true; // 放大镜所在	
	        bool = trigger.fireEvent('beforetrigger', trigger);
	        dbCaller=this.dbCaller|| (typeof caller === 'undefined' ? '' : caller);
	        if (bool == false) {
	            return;
	        }
	        this.setFieldStyle('background:#C6E2FF;');
	        var width = Ext.isIE ? screen.width * 0.7 * 0.9 : '80%',
	        height = Ext.isIE ? screen.height * 0.75 : '95%';
	        //针对有些特殊窗口显示较小
	        width =this.winWidth ? this.winWidth:width;
	        height=this.winHeight ? this.winHeight:height;
	        var dbwin = new Ext.window.Window({
	            id: 'dbwin',
	            title: '查找',
	            height: height,
	            width: width,
	            maximizable: true,
	            buttonAlign: 'center',
	            layout: 'anchor',
	            items: [{
	                tag: 'iframe',
	                frame: true,
	                anchor: '100% 100%',
	                layout: 'fit',
	                html: '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/oa/SchedulerResource.jsp?trigger=' + trigger.id + "&caller=" + dbCaller+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	            }],
	            buttons: [{
	                text: '关  闭',
	                iconCls: 'x-button-icon-close',
	                cls: 'x-btn-gray',
	                handler: function() {
	                    Ext.getCmp('dbwin').close();
	                }
	            },
	            {
	                text: '重置条件',
	                id: 'reset',
	                cls: 'x-btn-gray',
	                hidden: true,
	                handler: function() {
	                    var grid = Ext.getCmp('dbwin').el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
	                    grid.resetCondition();
	                    grid.getCount();
	                }
	            }]
	        });
	        dbwin.show();
	        trigger.lastTriggerId = null;
	
	}
});