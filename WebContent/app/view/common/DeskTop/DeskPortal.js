Ext.define('erp.view.common.DeskTop.DeskPortal', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.deskportal',
    requires: [
        'Ext.layout.component.Body','erp.view.common.DeskTop.DropZone'
    ], 
    cls: 'x-portal',
    bodyCls: 'x-portal-body',
    defaultType: 'portalcolumn',
    componentLayout: 'body',
    id:'deskportal',
    scrollOffset:18,
    autoScroll: true,
    initComponent : function() {
        var me = this;   
        this.layout = {
            type : 'column'
        };
        Ext.apply(this,{
          items:me.getOwnerSet()
        });
        this.callParent();
        this.addEvents({
            validatedrop: true,
            beforedragover: true,
            dragover: true,
            datarefresh: true,
            beforedrop: true,
            drop: true
        });
        this.on('drop', this.doLayout, this);
    },
    initEvents : function(){
        this.callParent();
        this.dd = Ext.create('erp.view.common.DeskTop.DropZone', this, this.dropConfig);
    },
    listeners :{
    	datarefresh:function(com ,type){
    		com.refresh(type);
    	}
    },
    beforeDestroy : function() {
        if (this.dd) {
            this.dd.unreg();
        }
        Ext.app.PortalPanel.superclass.beforeDestroy.call(this);
    },
    getOwnerSet:function(){
    	var arr=new Array();
    	Ext.Ajax.request({
			url : basePath + 'common/desktop/getOwner.action',
			method : 'get',
			async:false,
			callback : function(options, success, response){
				var res = new Ext.decode(response.responseText);
				var left_={
				  columnWidth:0.6,
				  items:[]
				},right_={
				  columnWidth:0.4,
				  items:[]	
				};
				if(res.length>0){
					Ext.Array.each(res,function(item,index){
					  var detno_=item.detno_,xtype,id;
					  
					  if(contains(item.xtype_,'#',true)){
					  		xtype = item.xtype_.split('#')[0];
					  		id = item.xtype_.split('#')[1];
					  }else{
					  		xtype = item.xtype_;
					  		id = item.xtype_;
					  }
					  if(detno_%2>0){
						  left_.items.push({xtype:xtype,pageCount:item.count_,id:id,xtype_:item.xtype_});
					  }else right_.items.push({xtype:xtype,pageCount:item.count_,id:id,xtype_:item.xtype_});
				    });
				}
				arr.push(left_);
				arr.push(right_);
			}
    	});
    	return arr;
    },
    refresh:function(type){
    	var me=this;
    	Ext.Array.each(me.items.items,function(item){
    		Ext.Array.each(item.items.items,function(c){
    			if(c[type]){
    				c._dorefresh(c);
    			}
    		});
    	});
    }
});
