Ext.define('erp.view.sysmng.upgrade.UpgradeBar',{
	extend: 'Ext.view.View', 
	alias: 'widget.upgradebar', 
	border:false,
	autoShow: true, 
	style: {
		position: 'absolute'
	},
	itemSelector:'li',
	activeItem:0,
	tpl:[ 
	     '<div id="uprogress" class="progressbar" align="center"><ul class="steps">',
	     '<tpl for=".">',
	     '<li class="{type}">',
	     '<span class="circle"></span>',
	     '<span class="font">{desc}</span>',
	     '</li>',
	     '</tpl></ul></div>'],
	     listeners:{
	    	 'itemclick':function(view,record,item,index){
	    	 	 var lis=document.getElementById('uprogress').getElementsByTagName('li');
	    	 	 for(var i=0;i<lis.length;i++){
	    	 	 	if(i==index){
	    	 	 		lis[i].setAttribute("class","normal active");
	    	 	 		
	    	 	 	}else{
	    	 	 		lis[i].setAttribute("class","normal");
	    	 	 	}
	    	 	 }
	    		 var upgradenavpanel=Ext.getCmp('upgradenavpanel');
	    		 upgradenavpanel.changeCard(upgradenavpanel,null,index);					
	    	 }
	     },
	     initComponent : function(){
	    	 var me=this;
		     me.store=Ext.create('Ext.data.Store', {
		    	 fields: [{name: 'itemId'},
		    	          {name:'desc'},{name:'type'}],
		    	          data: me.getData()
		     }),
	    	 this.callParent(arguments);
	     },
	     getData:function(dataview){
	    	 var data=[{ 
	    	 	 desc:'功能标识',
	    		 type:'active'
	    	},{
	    		 desc:'升级SQL',
	    		 type:'normal'
	 		}];
	 		return data;
	     }
});