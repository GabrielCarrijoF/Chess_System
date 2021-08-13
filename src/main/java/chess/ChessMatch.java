package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import com.sun.source.tree.NewArrayTree;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.stream.Collectors;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Quenn;
import pieces.Rook;

public class ChessMatch {

  private int turn;
  private Color currentPlayer;
  private Board board;
  private boolean check;
  private boolean checkMate;
  private ChessPiece evolution;


  private List<Piece> piecesOnTheBoard = new ArrayList<>();
  private List<Piece> capturedPieces = new ArrayList<>();


  public ChessMatch() {
    turn = 1;
    currentPlayer = Color.WHITE;
    board = new Board(8, 8);
    initialSetup();
  }

  public int getTurn() {
    return turn;
  }

  public boolean getCheck() {
    return check;
  }

  public Color getCurrentPlayer() {
    return currentPlayer;
  }

  public ChessPiece getEvolution() {
    return evolution;
  }

  public ChessPiece[][] getPieces() {
    ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        mat[i][j] = (ChessPiece) board.piece(i, j);
      }
    }
    return mat;
  }

  public boolean[][] possibleMoves(ChessPosition sourcePosition) {
    Position position = sourcePosition.tooPosition();
    validateSourcePosition(position);
    return board.piece(position).possibleMoves();
  }

  public ChessPiece performeChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
    Position source = sourcePosition.tooPosition();
    Position target = targetPosition.tooPosition();
    validateSourcePosition(source);
    validateTargetPosition(source, target);
    Piece capturedPiece = makeMove(source, target);

    if (testCheck(currentPlayer)) {
      undoMove(source, target, capturedPiece);
      throw new ChessExeption("You can't put yourself in check");
    }

    ChessPiece movedPiece = (ChessPiece) board.piece(target);

    // Evolution
    evolution = null;
    if (movedPiece instanceof Pawn) {
      if (movedPiece.getColor() == Color.WHITE && target.getRow() == 0
          || movedPiece.getColor() == Color.WHITE && target.getRow() == 7) {
        evolution = (ChessPiece) board.piece(target);
        evolution = replaceEvolutionPiece("Q");
      }
    }

    check = (testCheck(opponent(currentPlayer))) ? true : false;

    if (testCheckMate(opponent(currentPlayer))) {
      checkMate = true;
    } else {
      nextTurn();
    }
    return (ChessPiece) capturedPiece;
  }

  public ChessPiece replaceEvolutionPiece(String type) {
    if (evolution == null) {
      throw new IllegalStateException("There is no piece to be promoted");
    }
    if (!type.equals("B") && !type.equals("C") && !type.equals("Q") && !type.equals("T")) {
      throw new InvalidParameterException("Invalid type for evolution");
    }
    Position positionP = evolution.getChessPosition().tooPosition();
    Piece position = board.removePiece(positionP);
    piecesOnTheBoard.remove(position);

    ChessPiece newPiece = newPiece(type, evolution.getColor());
    board.placePiece(newPiece, positionP);
    piecesOnTheBoard.add(newPiece);
    return newPiece;
  }

  private ChessPiece newPiece(String type, Color color) {
    if (type.equals("B")) {
      return new Bishop(board, color);
    }
    if (type.equals("C")) {
      return new Knight(board, color);
    }
    if (type.equals("Q")) {
      return new Quenn(board, color);
    }
    return new Rook(board, color);

  }


  private Piece makeMove(Position source, Position target) {
    ChessPiece p = (ChessPiece) board.removePiece(source);
    p.increaseMoveCount();
    Piece capturedPiece = board.removePiece(target);
    board.placePiece(p, target);

    if (capturedPiece != null) {
      piecesOnTheBoard.remove(capturedPiece);
      capturedPieces.add(capturedPiece);
    }
    //Rook King
    if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
      Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
      Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
      ChessPiece rook = (ChessPiece) board.removePiece(sourceRook);
      board.placePiece(rook, targetRook);
      rook.increaseMoveCount();
    }

    //Rook Quenn
    if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
      Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
      Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
      ChessPiece rook = (ChessPiece) board.removePiece(sourceRook);
      board.placePiece(rook, targetRook);
      rook.increaseMoveCount();
    }
    return capturedPiece;
  }

  private void undoMove(Position source, Position target, Piece capturedPiece) {
    ChessPiece p = (ChessPiece) board.removePiece(target);
    p.decreaseMoveCount();
    board.placePiece(p, source);

    if (capturedPiece != null) {
      board.placePiece(capturedPiece, target);
      capturedPieces.remove(capturedPiece);
      piecesOnTheBoard.add(capturedPiece);
    }

    //Rook King
    if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
      Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
      Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
      ChessPiece rook = (ChessPiece) board.removePiece(targetRook);
      board.placePiece(rook, sourceRook);
      rook.decreaseMoveCount();
    }

    //Rook Quenn
    if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
      Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
      Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
      ChessPiece rook = (ChessPiece) board.removePiece(targetRook);
      board.placePiece(rook, sourceRook);
      rook.decreaseMoveCount();
    }

  }

  private void validateSourcePosition(Position position) {
    if (!board.hasPiece(position)) {
      throw new ChessExeption("There is no piece on source position");
    }
    if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
      throw new ChessExeption("The chosen piece is not yours");
    }
    if (!board.piece(position).isThereAnyPossibleMove()) {
      throw new ChessExeption("There is no possible moves for the chosen piece");
    }
  }

  private void validateTargetPosition(Position source, Position target) {
    if (!board.piece(source).possibleMoves(target)) {
      throw new ChessExeption("There chosen piece can't move to target position");
    }
  }

  private void nextTurn() {
    turn++;
    currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
  }

  private Color opponent(Color color) {
    return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
  }

  private ChessPiece king(Color color) {
    List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
        .collect(Collectors.toList());

    for (Piece p : list) {
      if (p instanceof King) {
        return (ChessPiece) p;
      }
    }
    throw new IllegalStateException("There is no" + color + "king on the board");
  }

  private boolean testCheck(Color color) {
    Position kingPosition = king(color).getChessPosition().tooPosition();
    List<Piece> opponentPieces = piecesOnTheBoard.stream()
        .filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
    for (Piece p : opponentPieces) {
      boolean[][] mat = p.possibleMoves();
      if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
        return true;
      }
    }
    return false;
  }

  private boolean testCheckMate(Color color) {
    if (!testCheck(color)) {
      return false;
    }
    List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
        .collect(Collectors.toList());
    for (Piece p : list) {
      boolean[][] mat = p.possibleMoves();
      for (int i = 0; i < board.getRows(); i++) {
        for (int j = 0; j < board.getColumns(); j++) {
          if (mat[i][j]) {
            Position source = ((ChessPiece) p).getChessPosition().tooPosition();
            Position target = new Position(i, j);
            Piece capturedPiece = makeMove(source, target);
            boolean testCheck = testCheck(color);
            undoMove(source, target, capturedPiece);
            if (!testCheck) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }


  private void placeNewPiece(char column, int row, ChessPiece piece) {
    board.placePiece(piece, new ChessPosition(column, row).tooPosition());
    piecesOnTheBoard.add(piece);
  }

  public void initialSetup() {
    placeNewPiece('a', 1, new Rook(board, Color.WHITE));
    placeNewPiece('d', 1, new Quenn(board, Color.WHITE));
    placeNewPiece('b', 1, new Knight(board, Color.WHITE));
    placeNewPiece('g', 1, new Knight(board, Color.WHITE));
    placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
    placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
    placeNewPiece('e', 1, new King(board, Color.WHITE, this));
    placeNewPiece('h', 1, new Rook(board, Color.WHITE));
    placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('h', 2, new Pawn(board, Color.WHITE));

    placeNewPiece('a', 8, new Rook(board, Color.BLACK));
    placeNewPiece('d', 8, new Quenn(board, Color.BLACK));
    placeNewPiece('b', 8, new Knight(board, Color.BLACK));
    placeNewPiece('g', 8, new Knight(board, Color.BLACK));
    placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
    placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
    placeNewPiece('e', 8, new King(board, Color.BLACK, this));
    placeNewPiece('h', 8, new Rook(board, Color.BLACK));
    placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('h', 7, new Pawn(board, Color.BLACK));


  }
}