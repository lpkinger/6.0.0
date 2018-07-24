Ext.require([
    'Ext.ux.PreviewPlugin'
]);
Ext.define('erp.view.common.DeskTop.CommonPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet', 
	alias: 'widget.commonportal',
	enableTools:true,
	cls:'task-portal',
	iconCls: 'main-schedule',
	animCollapse: false,
	activeRefresh:true,
	pageCount:10,
	initComponent : function(){
		var me=this,items= new Array();
		Ext.Ajax.request({
			url : basePath + 'common/desktop/getBench.action',
			params:{portid: me.id},
			method : 'get',
			async:false,
			callback : function(options, success, response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					me.text = res.title;
					me.fixcondition = res.condition;
					//校验后台activeTab数值
					res.activeTab = res.activeTab>=res.datas.length?res.datas.length-1:res.activeTab;
					var tabTitles ="";
					Ext.Array.each(res.datas,function(data,index){
						items.push({
							xtype:'erpDesktopGrid',
							id:me.id+'_grid'+index,
							data:data,
							pageCount:me.pageCount
						});	
						if(index == res.activeTab){
							tabTitles += "<button class=\"fa-tab-btn fa-tab-btn-active\">"+data.title+"</button>";;
						}else{
							tabTitles += "<button class=\"fa-tab-btn\">"+data.title+"</button>";
						}
					});	
					me.title = '<div class="div-left">'+res.title+'</div>'+tabTitles;
					Ext.apply(me,{
						items:[{
							xtype:'tabpanel',
							autoShow: true, 
							tabPosition:'top',
							minHeight:200,
							activeTab:res.activeTab,
							frame:true,
							bodyBorder: false,
							border: false,
							items:[items]
						}]
					});
				} else if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}
			}
    	});
		
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function() {
			var me = this;
			var buttons = me.el.dom.getElementsByClassName('fa-tab-btn');
			Ext.Array.each(buttons, function(btn, i) {
				btn.onclick = function(){
					me.el.dom.getElementsByClassName('fa-tab-btn-active')[0].classList.remove('fa-tab-btn-active');
					this.classList.add('fa-tab-btn-active')
					me.tabChange(i);
				}
			});
		}
	},
	tabChange: function(index) {
		this.down('tabpanel').setActiveTab(index);
	},
	getMore:function(){
		var url = "jsps/common/datalist.jsp?whoami="+this.id;
		if(this.fixcondition){
			url += "&"+parseUrl(this.fixcondition);
		}
		openTable(null,null,this.text,url,null,null);				
	},
	_dorefresh:function(panel){
		var activeTab=panel.down('tabpanel').getActiveTab();
		if(activeTab) activeTab.fireEvent('activate',activeTab);
	}
});