
import java.awt.* ;
import javax.swing.* ;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.geom.* ; //  Classes GeneralPath etc.


public class Heart extends JApplet implements Runnable
{
   int applet_width ,applet_height ;

   Thread  animation_thread  =  null ;

   boolean  thread_must_be_executed  =  false ;

   BufferedImage offscreen_drawing_surface ;
   BufferStrategy buffer_strategy ;

   public void init()
   {
      applet_width     =  getSize().width ;
      applet_height    =  getSize().height ;

      setIgnoreRepaint( true ) ;

      Canvas canvas_for_drawing = new Canvas();
      canvas_for_drawing.setIgnoreRepaint( true );
      canvas_for_drawing.setSize( applet_width, applet_height );
      
      add( canvas_for_drawing ) ; // Add canvas_for_drawing to this applet

      canvas_for_drawing.createBufferStrategy( 2 );
      buffer_strategy = canvas_for_drawing.getBufferStrategy();
   
      GraphicsEnvironment graphics_environment = 
                             GraphicsEnvironment.getLocalGraphicsEnvironment() ;
   
      GraphicsDevice graphics_device =
                             graphics_environment.getDefaultScreenDevice() ;
   
      GraphicsConfiguration graphics_configuration =
                             graphics_device.getDefaultConfiguration() ;
   
      offscreen_drawing_surface =
            graphics_configuration.createCompatibleImage( applet_width,
                                                          applet_height ) ;
   }


   public void start()
   {
      if ( animation_thread  ==  null )
      {
         animation_thread  =  new  Thread( this ) ;

         thread_must_be_executed  =  true ;

         animation_thread.start() ;
      }

      System.out.print( "\n Method start() executed. " ) ;
   }

   public void stop()
   {
      if ( animation_thread  !=  null )
      {
         animation_thread.interrupt() ;

         thread_must_be_executed  =  false ;

         animation_thread  =  null ;
      }

      System.out.print( "\n Method stop() executed. " ) ;
   }

   public void run()
   {
      System.out.print( "\n Method run() started." ) ;

      // Objects needed for rendering...
      Graphics   graphics = null ;
      Graphics2D graphics2D = null ;
      Color background_color = Color.PINK ;
         
      // Variables for counting frames per seconds
      int frames_per_second = 0;
      int frames_during_last_second = 0;
      long total_time = 0;
      long current_time = System.currentTimeMillis();
      long previous_time = current_time;

      
      GeneralPath  heart_shape  =  new  GeneralPath() ;

      heart_shape.moveTo( 0, 0 ) ;
      heart_shape.append( new Arc2D.Double( -100, -50, 100, 100, 0, 180,
                                            Arc2D.OPEN ), true ) ; 
      heart_shape.curveTo( -90, 60, -5, 90, 0, 150 ) ; // lower left side
      heart_shape.curveTo(   5, 90, 90, 60, 100, 0 ) ; // lower right side
      heart_shape.append( new Arc2D.Double(    0, -50, 100, 100, 0, 180,
                                            Arc2D.OPEN ), true ) ;
      heart_shape.closePath() ;

      double current_drawing_scale  =  1.00 ;

      boolean heart_is_enlarging  =  true ;

      while ( thread_must_be_executed  ==  true )
      {
         try
         {
            // clear back buffer...
            graphics2D = offscreen_drawing_surface.createGraphics();
            graphics2D.setColor( background_color );
            graphics2D.fillRect( 0, 0, applet_width, applet_height );

            // display frames per second...
            graphics2D.setFont( new Font( "Fantasy", Font.PLAIN, 12 ) );
            graphics2D.setColor( Color.BLACK );
            graphics2D.drawString( "BEATES PER SECOND: " + frames_per_second,
                                   20, 20 );

            graphics2D.setColor( Color.RED ) ;  //  The heart is red.

            
            graphics2D.translate( applet_width / 2, applet_height / 5 * 2 ) ;
            graphics2D.scale( current_drawing_scale, current_drawing_scale ) ;
            graphics2D.fill( heart_shape ) ;

            // The heart is now drawn. Let' blit the image and flip
            graphics = buffer_strategy.getDrawGraphics() ;
            graphics.drawImage( offscreen_drawing_surface, 0, 0, null ) ;
    
            if( ! buffer_strategy.contentsLost() )
            {
               buffer_strategy.show();
            }

            // Next we'll adjust the value of current_drawing_scale for the
            // next drawing operation.

            if ( heart_is_enlarging == true )
            {
               if ( current_drawing_scale < 1.25 )
               {
                  current_drawing_scale  =  current_drawing_scale + 0.01 ;
               }
               else
               {
                  heart_is_enlarging  =  false ;

                  try
                  {
                     Thread.sleep( 100 ) ;  // 0.1 seconds delay before
                                            // the heart starts shrinking.
                  }
                  catch ( InterruptedException  caught_exception )
                  {
                     thread_must_be_executed  =  false ;
                  }
               }
            }
            else
            {
               // The heart is shrinking.

               if ( current_drawing_scale  >  1.00 )
               {
                  current_drawing_scale  =  current_drawing_scale - 0.01 ;
               }
               else
               {
                  heart_is_enlarging  =  true ;

                  try
                  {
                     Thread.sleep( 1000 ) ;  // 1 second "rest" between beats
                  }
                  catch ( InterruptedException  caught_exception )
                  {
                     thread_must_be_executed  =  false ;
                  }
               }
            }
         }
         finally
         {
            if ( graphics != null ) 
            {
               graphics.dispose() ;  // release resource
            }
   
            if ( graphics2D != null ) 
            {
               graphics2D.dispose() ; // release resource
            }
         }

         try
         {
            Thread.sleep( 2 ) ;  
         }
         catch ( InterruptedException  caught_exception )
         {
            thread_must_be_executed  =  false ;
         }

         

         previous_time = current_time ;
         current_time  = System.currentTimeMillis() ;
         total_time += current_time - previous_time ;
   
         if( total_time > 1000 )
         {
            total_time -= 1000;
            frames_per_second = frames_during_last_second ;
            frames_during_last_second = 0 ;
         }
    
         frames_during_last_second ++ ;

      } // end of the almost eternal while loop

      System.out.print( "\n Method run() terminated. " ) ;
   }
}

