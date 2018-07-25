Ext.define('erp.view.sysmng.basicset.BasicSetBar',{
	extend: 'Ext.view.View', 
	alias: 'widget.basicsetbar', 
	border:false,
	style: {
		position: 'absolute'
	},
	itemSelector:'li',
	activeItem:0,
	tpl:[ 
	     '<div id="bprogress" class="progressbar" align="center"><ul class="steps">',
	     '<tpl for=".">',
	     '<li class="{type}">',
	     '<span class="font">{desc}</span>',
	     '</li>',
	     '</tpl></ul></div>'],
	     listeners:{
	    	 'itemclick':function(view,record,item,index){
	    	 	 var lis=document.getElementById('bprogress').getElementsByTagName('li');
	    	 	 for(var i=0;i<lis.length;i++){
	    	 	 	if(i==index){
	    	 	 		lis[i].setAttribute("class","normal active");
	    	 	 		
	    	 	 	}else{
	    	 	 		lis[i].setAttribute("class","normal");
	    	 	 	}
	    	 	 }
	    		 var basicnavpanel=Ext.getCmp('basicnavpanel');
	    		 basicnavpanel.changeCard(basicnavpanel,null,index);					
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
	    	 	 desc:'冻结字段',
	    		 type:'active'
	    	},{
	    		 desc:'数据字典',
	    		 type:'normal'
	 		}];
	 		return data;
	     }
});