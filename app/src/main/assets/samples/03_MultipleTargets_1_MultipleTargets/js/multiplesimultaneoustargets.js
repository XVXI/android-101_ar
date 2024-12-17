var World = {
    loaded: false,

    getDataFromNative: function getDataFromNativeFn(path){
        nativePath = path;
        this.init();
    },

    init: function initFn() {
        this.createOverlays();
    },

    createOverlays: function createOverlaysFn() {
        /*
            First a AR.TargetCollectionResource is created with the path to the Wikitude Target Collection(.wtc) file.
            This .wtc file can be created from images using the Wikitude Studio. More information on how to create them
            can be found in the documentation in the TargetManagement section.
            Each target in the target collection is identified by its target name. By using this
            target name, it is possible to create an AR.ImageTrackable for every target in the target collection.
         */
        this.targetCollectionResource = new AR.TargetCollectionResource("assets/tracker2.wtc", {
            onError: World.onError
        });

        /*
            This resource is then used as parameter to create an AR.ImageTracker. Optional parameters are passed as
            object in the last argument. In this case a callback function for the onTargetsLoaded trigger is set. Once
            the tracker loaded all of its target images this callback function is invoked. We also set the callback
            function for the onError trigger which provides a sting containing a description of the error.

            To enable simultaneous tracking of multiple targets 'maximumNumberOfConcurrentlyTrackableTargets' has
            to be set.
            to be set.
         */
        this.tracker = new AR.ImageTracker(this.targetCollectionResource, {
            maximumNumberOfConcurrentlyTrackableTargets: 5, // a maximum of 5 targets can be tracked simultaneously
            /*
                Disables extended range recognition.
                The reason for this is that extended range recognition requires more processing power and with multiple
                targets the SDK is trying to recognize targets until the maximumNumberOfConcurrentlyTrackableTargets
                is reached and it may slow down the tracking of already recognized targets.
             */
            extendedRangeRecognition: AR.CONST.IMAGE_RECOGNITION_RANGE_EXTENSION.OFF,
//            onTargetsLoaded: World.showInfoBar,
            onError: World.onError
        });

        new AR.Model(nativePath+"/Building_Base.wt3");
        this.modelWatbase = new AR.Model(nativePath+"/Building_Base.wt3", {
            onError: World.onError,
            scale: 0.0005,
            rotate: {
                z: -90
            },
             translate: {
                 x: -0.4,
                 y: 0.1
             },
        });

        new AR.Model(nativePath+"/Building.wt3");
        this.modelWatbuild = new AR.Model(nativePath+"/Building.wt3", {
            onError: World.onError,
            scale: 0.0005,
            rotate: {
                z: -90
            },
              translate: {
                  x: -0.4,
                  y: 0.1
              },
        });

        new AR.Model(nativePath+"/Building_Door_1.wt3");
        this.modelWatdoor1 = new AR.Model(nativePath+"/Building_Door_1.wt3", {
            onError: World.onError,
            scale: 0.0005,
              translate: {
                  x: -0.15,
                  y: 0.1
              },
        });

        this.modelWatdoor2 = new AR.Model(nativePath+"/Building_Door_1.wt3", {
            onError: World.onError,
            scale: 0.0005,
              translate: {
                  x: -0.65,
                  y: 0.1
              },
        });

        this.modelWatdoor3 = new AR.Model(nativePath+"/Building_Door_1.wt3", {
            onError: World.onError,
            scale: 0.0005,
              rotate: {
                  z: -90
              },
              translate: {
                  x: -0.4,
                  y: 0.525
              },
        });

        this.modelWatdoor4 = new AR.Model(nativePath+"/Building_Door_1.wt3", {
            onError: World.onError,
            scale: 0.0005,
              rotate: {
                  z: -90
              },
              translate: {
                  x: -0.4,
                  y: -0.3
              },
        });

        new AR.Model(nativePath+"/Status_Base.wt3");
        this.modelStatusbase = new AR.Model(nativePath+"/Status_Base.wt3", {
            onError: World.onError,
            scale: 0.0005,
            rotate: {
                z: -90
            },
              translate: {
                  x: 0.4,
                  y:-0.1
              },
        });

        new AR.Model(nativePath+"/Status.wt3");
        this.modelStatus = new AR.Model(nativePath+"/Status.wt3", {
            onError: World.onError,
            scale: 0.0005,
            rotate: {
                z: 90
            },
              translate: {
                  x: 0.4,
                  y:-0.1
              },
        });

        new AR.Model(nativePath+"/Status_Door.wt3");
        this.modelStatusDoor = new AR.Model(nativePath+"/Status_Door.wt3", {
            onError: World.onError,
            scale: 0.0005,
            rotate: {
                z: 90
            },
              translate: {
                  x: 0.4,
                  y:-0.1
              },
        });

        new AR.Model(nativePath+"/Status_Prop.wt3");
        this.modelStatusProp = new AR.Model(nativePath+"/Status_Prop.wt3", {
            onError: World.onError,
            scale: 0.0005,
            rotate: {
                z: 90
            },
              translate: {
                  x: 0.0,
                  y: 0.3
              },
        });

        this.watTrackable = new AR.ImageTrackable(this.tracker, "Wat Burapha Phiram_Marker", {
            drawables: {
                cam: [this.modelWatbase , this.modelWatbuild,
                        this.modelWatdoor1 ,this.modelWatdoor2 ,
                        this.modelWatdoor3 ,this.modelWatdoor4,
                        this.modelStatusbase , this.modelStatus,
                        this.modelStatusDoor , this.modelStatusProp
                ]
            },
            onImageRecognized: function(target){
                AR.platform.sendJSONObject({
                     action: "wat_recognized"
                 })
            },
            onImageLost:function(target){
                AR.platform.sendJSONObject({
                     action: "wat_lost"
                 })
            },
            onError: World.onError
        });

///////////////////////////////////////////////////////////////////
        new AR.Model(nativePath+"/Horwote_180320_final.wt3");
        this.modelHorwote = new AR.Model(nativePath+"/Horwote_180320_final.wt3", {
            onError: World.onError,
            scale: 0.01,
            rotate: {
                z: -25
            },
            onClick: function(target){
                 AR.platform.sendJSONObject({
                     action: "horwote_click"
                 })
             }
        });

        new AR.Model(nativePath+"/Base_Horwote.wt3");
        this.modelBaseHorwote = new AR.Model(nativePath+"/Base_Horwote.wt3", {
            onError: World.onError,
            scale: 0.1,
            rotate: {
                z: -90
            },
            translate: {
                x: -0.55 ,
                y: 0.55
            },
        });

        this.tree3 = new AR.Model(nativePath+"/Tree_Horwote_1.wt3", {
            onError: World.onError,
            scale: 0.1,
            translate: {
                x: -0.35 ,
                y: -0.2
            },
        });

        this.tree5 = new AR.Model(nativePath+"/Tree_Horwote_1.wt3", {
            onError: World.onError,
            scale: 0.1,
            translate: {
                x: -0.425 ,
                y: 0.3
            },
        });

        this.tree6 = new AR.Model(nativePath+"/Tree_Horwote_1.wt3", {
            onError: World.onError,
            scale: 0.1,
            translate: {
                x: -0.225 ,
                y: 0.4
            },
        });

        this.tree9 = new AR.Model(nativePath+"/Tree_Horwote_1.wt3", {
            onError: World.onError,
            scale: 0.1,
            translate: {
                x: 0.3 ,
                y: 0.3
            },
        });

        this.tree10 = new AR.Model(nativePath+"/Tree_Horwote_1.wt3", {
            onError: World.onError,
            scale: 0.1,
            translate: {
                x: 0.45 ,
                y: -0.4
            },
        });

        this.tree12 = new AR.Model(nativePath+"/Tree_Horwote_1.wt3", {
            onError: World.onError,
            scale: 0.1,
            translate: {
                x: 0.3 ,
                y: -0.15
            },
        });

        this.hstage = new AR.Model(nativePath+"/Stage.wt3", {
                    onError: World.onError,
            scale: 2,
            translate: {
                x: -0.15 ,
                y: -0.425
            },
        });

        this.clockTower = new AR.Model(nativePath+"/Clock_Tower.wt3", {
            onError: World.onError,
            scale: 0.05,
            translate: {
                x: -0.05 ,
                y: 0.3
            },
        });

        this.horTrackable = new AR.ImageTrackable(this.tracker, "Horwote_Marker", {
            drawables: {
                cam: [this.modelHorwote ,this.modelBaseHorwote  ,
                this.tree3,
                this.tree5, this.tree6,
                this.tree9,
                this.tree10, this.tree12,
                this.hstage , this.clockTower]
            },
            onImageRecognized: function(target){
                AR.platform.sendJSONObject({
                     action: "horwote_recognized"
                 })
            },
            onImageLost:function(target){
                AR.platform.sendJSONObject({
                     action: "horwote_lost"
                 })
            },
            onError: World.onError
        });

///////////////////////////////////////////////////////////////////
        new AR.Model(nativePath+"/Aq_Floor.wt3");
        this.modelAq_floor = new AR.Model(nativePath+"/Aq_Floor.wt3", {
            onError: World.onError,
            scale: 0.1
        });

        new AR.Model(nativePath+"/Aq_Build.wt3");
        this.modelAq_build = new AR.Model(nativePath+"/Aq_Build.wt3", {
            onError: World.onError,
            scale: 0.1,
            onClick: function(target){
                 AR.platform.sendJSONObject({
                     action: "aq_click"
                 })
             }
        });

        new AR.Model(nativePath+"/Aq_Bar.wt3");
        this.modelAq_bar = new AR.Model(nativePath+"/Aq_Bar.wt3", {
            onError: World.onError,
            scale: 0.1
        });

        new AR.Model(nativePath+"/Aq_Fream1_1.wt3");
        this.modelAq_f1_1 = new AR.Model(nativePath+"/Aq_Fream1_1.wt3", {
            onError: World.onError,
            scale: 0.1
        });

        new AR.Model(nativePath+"/Aq_Fream1_2.wt3");
        this.modelAq_f1_2 = new AR.Model(nativePath+"/Aq_Fream1_2.wt3", {
            onError: World.onError,
            scale: 0.1
        });

        new AR.Model(nativePath+"/Aq_Fream1_3.wt3");
        this.modelAq_f1_3 = new AR.Model(nativePath+"/Aq_Fream1_3.wt3", {
            onError: World.onError,
            scale: 0.1
        });

        new AR.Model(nativePath+"/Aq_Fream2_1.wt3");
        this.modelAq_f2_1 = new AR.Model(nativePath+"/Aq_Fream2_1.wt3", {
            onError: World.onError,
            scale: 0.1
        });

        new AR.Model(nativePath+"/Aq_Fream2_2.wt3");
        this.modelAq_f2_2 = new AR.Model(nativePath+"/Aq_Fream2_2.wt3", {
            onError: World.onError,
            scale: 0.1
        });

        new AR.Model(nativePath+"/Aq_Fream2_3.wt3");
        this.modelAq_f2_3 = new AR.Model(nativePath+"/Aq_Fream2_3.wt3", {
            onError: World.onError,
            scale: 0.1
        });

        this.aq_video = new AR.VideoDrawable(nativePath+"/aq2video.mp4", 0.327, {
            onError: World.onError ,
             translate: {
                 z: 0.17
             }
        });

        this.aqTrackable = new AR.ImageTrackable(this.tracker, "Aquarium_Marker", {
            drawables: {
                cam: [this.modelAq_floor , this.modelAq_build, this.modelAq_bar,
                        this.modelAq_f1_1 , this.modelAq_f1_2, this.modelAq_f1_3,
                        this.modelAq_f2_1 , this.modelAq_f2_2, this.modelAq_f2_3,
                        World.aq_video]
            },
            onImageRecognized: function(target){
                if (this.hasVideoStarted) {
                    World.aq_video.resume();
                } else {
                    this.hasVideoStarted = true;
                    World.aq_video.play(-1);
                }
                AR.platform.sendJSONObject({
                     action: "aq_recognized"
                 })
            },
            onImageLost:function(target){
                World.aq_video.pause();
                AR.platform.sendJSONObject({
                     action: "aq_lost"
                 })
            },
            onError: World.onError
        });
    },

    onError: function onErrorFn(error) {
        alert(error);
    },
};

var nativePath = "";

//function WorldInit() {
//      World.init();
//};
