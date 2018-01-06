package vue;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import tools.MazeImageProvider;
import tools.MazeTreasureImage;
import model.Coord;
import model.Couleur;
import model.PieceIHMs;
import model.Treasure;
import model.TreasureIHMs;
import net.miginfocom.swing.MigLayout;
import model.CouloirIHM;
import model.TreasureIHM;
import model.observable.MazeGame;
import controler.MazeGameControlers;
import controler.controlerLocal.MazeGameControler;

public class MazeGameGUI extends JFrame implements MouseListener, MouseMotionListener, Observer {

	/**
	 * default serial version uid
	 */
	private static final long serialVersionUID = 1L;
	private JLayeredPane layeredPane;
	private JLabel tresorToCatch;
	private JLayeredPane mazeContainer;
	private JPanel generalBoard;
	private JPanel menu;
	private JPanel scores;
	private JPanel activePlayer;
	private Icon imageTreasureToCatch;
	private Box b1,b2,b3;
	private JPanel mazeBoard;
	private JLabel pawn = null;
	private JLabel scoreMario;
	private JLabel scoreYoshi;
	private JLabel scoreLuigi;
	private JLabel scoreToad;
	private JLabel player;
	private ImagePanel contentPane;
	private int xAdjustment;
	private JButton rotateLeftButton, rotateRightButton;
	private JButton okButton; 
	private JRadioButton nb2Button, nb3Button, nb4Button;
	private int yAdjustment;
	private int xOrigine;
	private ButtonGroup grpButton;
	private int yOrigine;
	private int nbPlayer = 2;
	private MazeGameControlers mazeGameControler;
	private Component previouslyHoveredComponent;
	List<TreasureIHMs> treasureIHMs;
	private Dimension dim;
	private final Integer COULOIR_LAYER = 0;
	private final Integer TREASURE_LAYER = 1;
	private final Integer PAWN_LAYER = 2;
	private JFrame f1;

	
	public MazeGameGUI(Dimension dim) {
		
		this.dim = dim;
		Dimension windowSize = new Dimension(950,700);		
		
		// on cree un conteneur general qui acceuillera le tableau de jeu + l'element dragge
		mazeContainer = new JLayeredPane();
		mazeContainer.setPreferredSize(windowSize);
		mazeContainer.setBounds(0, 0, windowSize.width, windowSize.height);
		
		// on cree le container du menu
	    b1 = Box.createHorizontalBox();
		b1.setOpaque(true); // background gris desactive
		grpButton = new ButtonGroup();
		nb2Button = new JRadioButton("2 joueurs");
		nb3Button = new JRadioButton("3 joueurs");
		nb4Button = new JRadioButton("4 joueurs");

		nb2Button.setOpaque(false);
		nb3Button.setOpaque(false);
		nb4Button.setOpaque(false);
		
		// ajout des boutons radio dans le groupe bg
		grpButton.add(nb2Button);
		grpButton.add(nb3Button);
		grpButton.add(nb4Button);
		
		nb2Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nbPlayer = 2;
			}
		});
		
		nb3Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nbPlayer = 3;
			}
		});
		
		nb4Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nbPlayer = 4;
			}
		});
		b1.add(nb2Button);
		b1.add(nb3Button);
		b1.add(nb4Button);
		nb2Button.setSelected(true);
		
		b2 = Box.createHorizontalBox();
		b2.setOpaque(false); // background gris desactive
		// Lancer le jeu
		okButton = new JButton("Lancer");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initMazeGame(nbPlayer);	
			}			
		});
		b2.add(okButton);
			
		b3 = Box.createVerticalBox();
		b3.setOpaque(false); // background gris desactive
		b3.add(b1);
		b3.add(b2);
				
		b3.setBorder(new EmptyBorder(100, 0, 0, 0));
		b2.setBorder(new EmptyBorder(10,0,0,0));
		
		File g = new File("");
		String path = "/src/images/";
		String ret = g.getAbsolutePath() + path + "bg.jpg";
		
		//ImagePanel panel = new ImagePanel(new ImageIcon(ret).getImage());	
	   	contentPane = new ImagePanel(new ImageIcon(ret).getImage());
	    contentPane.add(b3);
		contentPane.setPreferredSize(windowSize);
		setContentPane(contentPane);	
	}
	
	public void initMazeGame(int nbPlayer) {
		Dimension windowSize = new Dimension(950,1000);		
		Icon imageIcon;
		Icon disabledIcon;
		List<CouloirIHM> couloirIHMs;
		List<PieceIHMs> pieceIHMs;
		JLabel couloir;
		JLabel treasure;
		JLabel tresorCard;
		CouloirIHM extraCard;
		JLayeredPane extraCardPane;
		JLabel extraCardImage;	
		
		
		setContentPane(mazeContainer);
		repaint();
		pack();
		
		MazeGame mazeGame;	
		MazeGameControlers mazeGameControler;
		
		mazeGame = new MazeGame(nbPlayer);
		mazeGameControler = new MazeGameControler(mazeGame);
		mazeGame.addObserver((Observer) this);
		this.mazeGameControler = mazeGameControler;
		// on initialise le controleur
		couloirIHMs = mazeGameControler.getCouloirsIHMs();
		pieceIHMs = mazeGameControler.getPiecesIHMs();
		treasureIHMs = mazeGameControler.getTreasuresIHMs();

		//On cree une grille de 2 par 2 (4 cases)
		//Le plateau sera dans la premiere case, les elements de jeu dans les autres
		generalBoard = new JPanel(new MigLayout());

		// On cree une grille de 7 par 7 (49 cases)
		mazeBoard = new JPanel(new GridLayout(7,7));
		
		//On cree on grille de 2 par 2 pour les scores
		scores = new JPanel(new GridLayout(2,2));
		//On met une bordure pour le délimiter visuellement
		scores.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		//On donne une taille au tableau des scores
		scores.setPreferredSize(new Dimension(200,100));
		
		//On initialise le score de chaque joueur
		scoreMario = new JLabel();
		scoreMario.setText("Mario : 0"); //+ mazeGameControler.getRedPlayerScore());
		scoreYoshi = new JLabel();
		scoreYoshi.setText("Yoshi : 0"); //+ mazeGameControler.getYellowPlayerScore());
		scoreLuigi = new JLabel();
		scoreLuigi.setText("Luigi : 0"); //+ mazeGameControler.getBluePlayerScore());
		scoreToad = new JLabel();
		scoreToad.setText("Toad : 0"); //+ mazeGameControler.getGreenPlayerScore());
		
		//On ajoute les scores dans le tableau
		scores.add(scoreMario);
		scores.add(scoreLuigi);
		if(nbPlayer == 3 || nbPlayer == 4) {
			scores.add(scoreYoshi);
		}
		if(nbPlayer == 4) {
			scores.add(scoreToad);
		}
		
		//On crée le panel du joueur actif
		activePlayer = new JPanel(new BorderLayout());
		
		//On crée le label sur lequel le joueur actif sera récupéré
		player = new JLabel();
		
		//On initialise le joueur devant jouer à "Mario"
		player.setText("Tour du joueur : Mario");
		
		//On ajoute le JLabel sur le JPanel
		activePlayer.add(player);
		
		//On definit la taille de la grille generale
		generalBoard.setPreferredSize(windowSize);
		generalBoard.setBounds(0, 0, windowSize.width, windowSize.height);
		
		//On crée le JLabel du tresor à attraper
		tresorToCatch = new JLabel();
		
		
		//On cree une image pour la pile des cartes des tresors
		imageIcon = new ImageIcon(MazeImageProvider.getImageCardTresorsFile("DosJeu"));
		//On cree la zone pour la pile de cartes
		tresorCard = new JLabel(imageIcon);
		
		
		
		//On cree la carte supplementaire, recuperant la deuxieme piece de la liste
		//On garde le côte aleatoire comme la liste est aleatoire
		//Il faut la deuxieme car la premiere est un angle de depart
		extraCard = mazeGameControler.getExtraCorridorIHM();

		//Bouton de rotation gauche
		rotateLeftButton = new JButton("Gauche");
		rotateLeftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extraCard.rotateLeft();
			}
		});
		//Bouton de rotation droit
		rotateRightButton = new JButton("Droite");
		rotateRightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extraCard.rotateRight();
			}
		});
		// on cree un panneau contenant differents plans
		extraCardPane = new JLayeredPane();
		extraCardPane.setPreferredSize(new Dimension(100, 100));

		// on cree une image de couloir pour la piece supplementaire
		imageIcon = new ImageIcon(MazeImageProvider.getImageFile(
			"Couloir",
			extraCard.isNorthOpened(),
			extraCard.isSouthOpened(),
			extraCard.isEastOpened(),
			extraCard.isWestOpened(),
			false
		));
		disabledIcon = new ImageIcon(MazeImageProvider.getImageFile(
			"Couloir",
			extraCard.isNorthOpened(),
			extraCard.isSouthOpened(),
			extraCard.isEastOpened(),
			extraCard.isWestOpened(),
			true
		));
		extraCardImage = new JLabel(imageIcon);
		extraCardImage.setDisabledIcon(disabledIcon);	
		
		// on parametre la taille et la position de la piece supplementaire
		extraCardImage.setPreferredSize(new Dimension(100, 100));
		extraCardImage.setBounds(0, 0, 100, 100);

		// on ajoute le couloir en arriere-plan
		extraCardPane.add(extraCardImage, COULOIR_LAYER);

		// position et taille du plateau de jeu -> on recupere les dimensions passees en parametres
		mazeBoard.setPreferredSize(dim);
		mazeBoard.setBounds(0, 0, dim.width, dim.height);

		// creation des couloirs a partir du modele
		for(CouloirIHM couloirIHM : couloirIHMs) {

			// on cree un panneau contenant differents plans
			this.layeredPane = new JLayeredPane();
			this.layeredPane.setPreferredSize(new Dimension(100, 100));

			// on cree une image de couloir
			imageIcon = new ImageIcon(MazeImageProvider.getImageFile(
				"Couloir",
				couloirIHM.isNorthOpened(),
				couloirIHM.isSouthOpened(),
				couloirIHM.isEastOpened(),
				couloirIHM.isWestOpened(),
				false
			));
			disabledIcon = new ImageIcon(MazeImageProvider.getImageFile(
				"Couloir",
				couloirIHM.isNorthOpened(),
				couloirIHM.isSouthOpened(),
				couloirIHM.isEastOpened(),
				couloirIHM.isWestOpened(),
				true
			));
			couloir = new JLabel(imageIcon);
			couloir.setDisabledIcon(disabledIcon);

			// si on veut entourer les couloirs fixes
			/*
			 * if(couloirIHM.isFixed()) {
			 * this.couloir.setBorder(BorderFactory.createLineBorder
			 * (Color.blue)); }
			 */
			
			// pour chaque case on ajoute paramètre dimension et position du couloir
			couloir.setPreferredSize(new Dimension(100, 100));
			couloir.setBounds(0, 0, 100, 100);

			// on ajoute le couloir en arriere-plan
			this.layeredPane.add(couloir, COULOIR_LAYER);

			// on ajoute les differents plans au plateau
			this.mazeBoard.add(this.layeredPane);
		}
		
		// creation des pions a partir du modele
		for(PieceIHMs pieceIHM : pieceIHMs) {

			// si on est sur la position d'un pion
			// on cree un pion a chaque coin du jeu
			this.pawn = new JLabel(
					new ImageIcon(MazeImageProvider.getImageFile(
							"Pion",
							pieceIHM.getCouleur()
					))
			);

			this.pawn.setPreferredSize(new Dimension(100, 100));
			this.pawn.setBounds(0, 0, 100, 100);
			this.pawn.setOpaque(false);

			// TODO moche ajouter tests
			((JLayeredPane) this.mazeBoard.getComponent(pieceIHM.getX() + 7
					* pieceIHM.getY())).add(this.pawn, PAWN_LAYER);
		}
		
		for(TreasureIHMs treasureIHM : treasureIHMs){
			treasure = new JLabel (new ImageIcon(MazeImageProvider.getImageFile(treasureIHM.getTreasureId())));
			treasure.setPreferredSize(new Dimension(100, 100));
			treasure.setBounds(0, 0, 100, 100);
			treasure.setOpaque(false);
			//TODO moche ajouter tests
			((JLayeredPane)this.mazeBoard.getComponent(treasureIHM.getTreasureX() + 7*treasureIHM.getTreasureY())).add(treasure, TREASURE_LAYER);
		}
		Treasure treasureToCatch = this.mazeGameControler
				.currentTreasureToCatch();
		imageTreasureToCatch = new ImageIcon(MazeImageProvider.getImageFile(treasureToCatch.getTreasureId()));
		//On cree la zone pour la pile de cartes
		tresorToCatch.setIcon(imageTreasureToCatch);

		generalBoard.add(tresorToCatch,"pos 0.892al 0.458al");
		generalBoard.add(mazeBoard, "pos 0 0");
		generalBoard.add(tresorCard, "pos 0.93al 0.45al");
		generalBoard.add(extraCardPane, "pos 0.92al 0.03al"); //AbsoluteLayout : on positionne en pourcentage de la fenetre
		generalBoard.add(rotateLeftButton, "pos 0.915al 0al");
		generalBoard.add(rotateRightButton, "pos 0.91al 0.135al");
		generalBoard.add(activePlayer, "pos 0.94al 0.25al");
		generalBoard.add(scores, "pos 0.98al 0.65al");
		generalBoard.add(tresorToCatch,"pos 0.909al 0.3al");
		mazeContainer.add(generalBoard);
		// TODO n'ecouter que les pions eventuellement
		mazeBoard.addMouseListener(this);
		mazeBoard.addMouseMotionListener(this);
	}

	public void mousePressed(MouseEvent e) {
		JLayeredPane parent;
		Component componentPressed =  this.mazeBoard.findComponentAt(e.getX(), e.getY());
		boolean isOkDest;
		int xDest, yDest;
		List<Coord> reacheableCoords;

		this.pawn = null;

		if (componentPressed instanceof JPanel || componentPressed == null) {
			return;
		}

		if (componentPressed != null) {
			JLayeredPane destinationPane = (JLayeredPane) componentPressed
					.getParent();

			// on ne prend que la couche la plus haute
			if (destinationPane.getLayer(componentPressed) == COULOIR_LAYER
					|| destinationPane.getLayer(componentPressed) == TREASURE_LAYER) {
				return;
			}

			Point parentLocation = componentPressed.getParent().getLocation();
			xAdjustment = parentLocation.x - e.getX();
			yAdjustment = parentLocation.y - e.getY();
			this.pawn = (JLabel) componentPressed;
			parent = (JLayeredPane) componentPressed.getParent();
			xOrigine = e.getX() / (this.mazeBoard.getHeight() / 7);
			yOrigine = e.getY() / (this.mazeBoard.getHeight() / 7);
			this.pawn.setLocation(e.getX() + xAdjustment, e.getY()
					+ yAdjustment);
			this.pawn.setSize(this.pawn.getWidth(), this.pawn.getHeight());

			if (parent != null) {
				this.mazeContainer.add(this.pawn, JLayeredPane.DRAG_LAYER);

				// TODO a reprendre pour generation chemin possible
				// on grise les cases ou on ne peut pas se deplacer
				reacheableCoords = this.mazeGameControler.findPath(new Coord(xOrigine, yOrigine));
				for (Component component : this.mazeBoard.getComponents()) {
					xDest = component.getX() / (this.mazeBoard.getHeight()/7);
					yDest = component.getY() / (this.mazeBoard.getHeight()/7);
					isOkDest = false;
					for(Coord coord : reacheableCoords) {
						if(coord.x == xDest && coord.y == yDest) {
							isOkDest = true;
						}
					}
					if(!isOkDest) {
						if(((JLayeredPane) component).getComponentsInLayer(COULOIR_LAYER).length > 0) {
							//TODO moche ajouter un test
							((JLayeredPane) component).getComponentsInLayer(COULOIR_LAYER)[0].setEnabled(false);
						}
					}
				}
			}
		}
	}

	public void mouseDragged(MouseEvent me) {
		 Component hoveredComponent;
		 JLayeredPane layeredPane;
		 JLabel corridorImage;

		if (this.pawn == null) {
			return;
		}
		this.pawn.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);

		hoveredComponent = mazeBoard.getComponentAt(me.getX(), me.getY());

		 // si on est en dehors du plateau
		 if(hoveredComponent == null) {
			 return;
		 }

		 // affichage d'effets au survol d'une case
		 if(previouslyHoveredComponent == null || !previouslyHoveredComponent.equals(hoveredComponent)){
			 if(previouslyHoveredComponent != null) {
				 layeredPane = (JLayeredPane) previouslyHoveredComponent;
				 corridorImage = (JLabel) layeredPane.getComponentsInLayer(COULOIR_LAYER)[0];
				 corridorImage.setBorder(null);
			 }

			 previouslyHoveredComponent = hoveredComponent;

			layeredPane = (JLayeredPane) previouslyHoveredComponent;
			 // FIXME moche
			 corridorImage = (JLabel) layeredPane.getComponentsInLayer(COULOIR_LAYER)[0];
			 corridorImage.setBorder(BorderFactory.createLineBorder(Color.yellow));

		 }
	 }

	public void mouseReleased(MouseEvent e) {
		 //mazeBoard.getHeight() donne la hauteur en pixels de la fenetre
		 //on divise getX() par layeredPane.getHeight()/7 pour savoir dans quelle case on a deplace la piece
		 int destinationX = e.getX()/(mazeBoard.getHeight()/7);
		 int destinationY = e.getY()/(mazeBoard.getHeight()/7);

		 JLayeredPane layeredPane;
		 JLayeredPane parentComponentHere;
		 JLabel corridorImage;

		 if (pawn == null) {
			 return;
		 }

		// on retire l'effet visuel du hover
		 layeredPane = (JLayeredPane) previouslyHoveredComponent;
		 corridorImage = (JLabel) layeredPane.getComponentsInLayer(COULOIR_LAYER)[0];
		 corridorImage.setBorder(null);

		 boolean isMoveOK = mazeGameControler.move(
			 new Coord(xOrigine, yOrigine),
			 new Coord(destinationX, destinationY)
		 );
		 
		 Treasure treasureToCatch = this.mazeGameControler
					.currentTreasureToCatch();
	
		imageTreasureToCatch = new ImageIcon(MazeImageProvider.getImageFile(treasureToCatch.getTreasureId()));
		//On cree la zone pour la pile de cartes
		tresorToCatch.setIcon(imageTreasureToCatch);

		if (isMoveOK) {
			System.out.println("déplacement OK");
			Component componentHere = this.mazeBoard.findComponentAt(e.getX(),
					e.getY());
			parentComponentHere = (JLayeredPane) componentHere.getParent();
			if (parentComponentHere.getComponentsInLayer(TREASURE_LAYER).length > 0) {	
				
				if (destinationX == treasureToCatch.getTreasureX()
						&& destinationY == treasureToCatch.getTreasureY()) {
					this.mazeGameControler.treasureCatchedPlateau(treasureToCatch);
					this.mazeGameControler.setCurrentTreasureToCatch(null);
					//Lors d'un changement de score, on met à jour l'affichage du tableau
					scoreMario.setText("Mario : " + mazeGameControler.getRedPlayerScore());
					scoreLuigi.setText("Luigi : " + mazeGameControler.getBluePlayerScore());
					if(nbPlayer==3 || nbPlayer==4) {
						scoreYoshi.setText("Yoshi : " + mazeGameControler.getYellowPlayerScore());
					}
					if(nbPlayer==4) {
						scoreToad.setText("Toad : " + mazeGameControler.getGreenPlayerScore());
					}

				}
			}
			this.mazeGameControler.switchJoueur();
			if(mazeGameControler.getColorCurrentPlayer() == Couleur.ROUGE) {
				player.setText("Tour du joueur : Mario");
			}
			else if(mazeGameControler.getColorCurrentPlayer() == Couleur.BLEU) {
				player.setText("Tour du joueur : Luigi");
			}
			else if(mazeGameControler.getColorCurrentPlayer() == Couleur.JAUNE) {
				player.setText("Tour du joueur : Yoshi");
			}
			else if(mazeGameControler.getColorCurrentPlayer() == Couleur.VERT) {
				player.setText("Tour du joueur : Toad");
			}
		}
	 }

	public void mouseClicked(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void update(Observable o, Object arg) {
		if(this.mazeBoard == null) {
			return;
		}
		if(((LinkedList<PieceIHMs>)arg).getFirst() instanceof PieceIHMs) {
			List<PieceIHMs> piecesIHM = (List<PieceIHMs>) arg;
			for (PieceIHMs pieceIHM : piecesIHM) {
				//On récupère la piece sur le board (son layerded pane)
				this.layeredPane = (JLayeredPane) this.mazeBoard.getComponent(7
						* pieceIHM.getY() + pieceIHM.getX());

				//On enlève le pion
				if (this.layeredPane.getComponentsInLayer(PAWN_LAYER).length != 0) {
					for (int i = 0; i < this.layeredPane
							.getComponentsInLayer(PAWN_LAYER).length; i++) {
						this.layeredPane.remove(this.layeredPane
								.getComponentsInLayer(PAWN_LAYER)[i]);
					}
				}
				// on enlève le pion du drag layer
				if (this.mazeContainer
						.getComponentsInLayer(JLayeredPane.DRAG_LAYER).length != 0) {
					for (int i = 0; i < this.mazeContainer
							.getComponentsInLayer(JLayeredPane.DRAG_LAYER).length; i++) {
						this.mazeContainer.remove(this.mazeContainer
								.getComponentsInLayer(JLayeredPane.DRAG_LAYER)[i]);
					}
				}

				// on recrée le pion
				this.pawn = new JLabel(new ImageIcon(
						MazeImageProvider.getImageFile("Pion",
								pieceIHM.getCouleur())));
				this.pawn.setPreferredSize(new Dimension(100, 100));
				this.pawn.setBounds(0, 0, 100, 100);
				this.pawn.setOpaque(false);

				// on rajoute le pion
				this.layeredPane.add(this.pawn, PAWN_LAYER);
			}
		}

		if(((LinkedList<TreasureIHMs>)arg).getFirst() instanceof TreasureIHMs) {
			List<TreasureIHMs> updatedList = (List<TreasureIHMs>) arg;
			JLabel treasure;

			//Suppression des trésors
			for (TreasureIHMs treasureIHM : treasureIHMs) {
				// on récupère le trésor
				this.layeredPane = (JLayeredPane) this.mazeBoard.getComponent(7
						* treasureIHM.getTreasureY() + treasureIHM.getTreasureX());

				//On le supprime
				if (this.layeredPane.getComponentsInLayer(TREASURE_LAYER).length != 0) {
					for (int i = 0; i < this.layeredPane
							.getComponentsInLayer(TREASURE_LAYER).length; i++) {
						this.layeredPane.remove(this.layeredPane
								.getComponentsInLayer(TREASURE_LAYER)[i]);
					}
				}
			}

			//Re-creaction des tresors
			for (TreasureIHMs treasureIHM : updatedList) {
				// on récupère le trésor
				this.layeredPane = (JLayeredPane) this.mazeBoard.getComponent(7
						* treasureIHM.getTreasureY() + treasureIHM.getTreasureX());

				//On le supprime
				if (this.layeredPane.getComponentsInLayer(TREASURE_LAYER).length != 0) {
					for (int i = 0; i < this.layeredPane
							.getComponentsInLayer(TREASURE_LAYER).length; i++) {
						this.layeredPane.remove(this.layeredPane
								.getComponentsInLayer(TREASURE_LAYER)[i]);
					}
				}

				//On recrée le trésor
				treasure = new JLabel(new ImageIcon(
						MazeImageProvider.getImageFile(treasureIHM.getTreasureId())));
				treasure.setPreferredSize(new Dimension(100, 100));
				treasure.setBounds(0, 0, 100, 100);
				treasure.setOpaque(false);
				// TODO moche ajouter tests
				((JLayeredPane) this.mazeBoard.getComponent(treasureIHM
						.getTreasureX() + 7 * treasureIHM.getTreasureY())).add(
						treasure, TREASURE_LAYER);
			}

		}

		 // on reautorise toutes les cases
		for (Component component : this.mazeBoard.getComponents()) {
			if (((JLayeredPane) component).getComponentsInLayer(COULOIR_LAYER).length > 0) {
				((JLayeredPane) component).getComponentsInLayer(COULOIR_LAYER)[0]
						.setEnabled(true);
			}
		}

		this.repaint();
		this.revalidate();
	}


}
