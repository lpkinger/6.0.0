/*
控制层,
所有逻辑代码都在这里写
 */
Ext.QuickTips.init();
Ext.define('erp.controller.common.Home', {
	extend: 'Ext.app.Controller',
	views: ['common.home.Viewport', 'core.form.HolidayDatePicker'],//声明该控制层要用到的view
	init: function(){ 
		var me=this;
		//每隔一分钟刷新[首页个人知会]
		Ext.defer(function(){
			var bench_note=Ext.getCmp('bench_note');
			if(bench_note){
				me.refreshPagingRelease();
			}
		}, 30000);	
		this.control({ 

		});
	},
	refreshPagingRelease:function(){
		var me = this;
		if(!Ext.getCmp('lock-win') && me.bench){
			me.bench._bench_note();     		    
		}
		setTimeout(function(){
			me.refreshPagingRelease();
		},30000);
	}
});

